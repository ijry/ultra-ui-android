package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPCompatibilityDiagnostics
import net.lingyun.ultraui.android.core.UPCompatibilityEvent
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class UPPickerInputSupportTest {
    private val events = mutableListOf<UPCompatibilityEvent>()
    private val diagnostics = UPCompatibilityDiagnostics { events += it }

    @Test
    fun objectColumnsResolveConfirmedValuesBackToTheirLabels() {
        val columns = listOf<UPRawValue>(
            listOf(
                mapOf("text" to "北京", "value" to "bj"),
                mapOf("text" to "上海", "value" to "sh"),
                mapOf("text" to "广州", "value" to "gz"),
            ),
        )
        assertEquals("上海", upPickerInputLabel(columns, listOf("sh"), "text", "value"))
        assertEquals("北京/广州", upPickerInputLabel(columns, listOf("bj", "gz"), "text", "value"))
        assertEquals("", upPickerInputLabel(columns, emptyList(), "text", "value"))
    }

    @Test
    fun plainColumnsJoinTheConfirmedValuesVerbatim() {
        val columns = listOf<UPRawValue>(listOf("周一", "周二"))
        assertEquals("周二", upPickerInputLabel(columns, listOf("周二"), "text", "value"))
        assertEquals("周一/周二", upPickerInputLabel(columns, listOf("周一", "周二"), "text", "value"))
        assertEquals("", upPickerInputLabel(emptyList(), emptyList(), "text", "value"))
    }

    @Test
    fun objectColumnsHonourCustomKeyAndValueNames() {
        val columns = listOf<UPRawValue>(
            listOf(mapOf("label" to "男", "id" to 1), mapOf("label" to "女", "id" to 2)),
        )
        assertEquals("女", upPickerInputLabel(columns, listOf(2), "label", "id"))
        // The picker emits strings for numeric ids too, so the lookup stays loose.
        assertEquals("男", upPickerInputLabel(columns, listOf("1"), "label", "id"))
    }

    @Test
    fun datetimeLabelsUseThePerModeFallbackFormats() {
        // dayjs formats in local time, so the expectation is built from the same calendar.
        val millis = localMillis(2023, 11, 15, 6, 13, 20)
        assertEquals("2023-11-15", upDatetimeInputLabel("date", "", millis))
        assertEquals("2023-11", upDatetimeInputLabel("year-month", "", millis))
        assertEquals("2023-11-15 06", upDatetimeInputLabel("datehour", "", millis))
        assertEquals("2023-11-15 06:13", upDatetimeInputLabel("datetime", "", millis))
        assertEquals("2023-11-15 06:13:20", upDatetimeInputLabel("datetimesecond", "", millis))
    }

    @Test
    fun datetimeLabelsShowTimeModesVerbatimAndHonourExplicitFormats() {
        assertEquals("09:30", upDatetimeInputLabel("time", "", "09:30"))
        assertEquals("09:30:15", upDatetimeInputLabel("timesecond", "YYYY/MM/DD", "09:30:15"))
        assertEquals("2023/11/15", upDatetimeInputLabel("date", "YYYY/MM/DD", localMillis(2023, 11, 15, 6, 13, 20)))
        // A formatted string round-trips through the same parser uview's dayjs accepts.
        assertEquals("2023-11-15", upDatetimeInputLabel("date", "", "2023-11-15 06:13:20"))
        assertEquals("2023-11-15 06:13", upDatetimeInputLabel("datetime", "", "2023-11-15 06:13"))
    }

    @Test
    fun datetimeLabelsAreEmptyForUnsetValues() {
        assertEquals("", upDatetimeInputLabel("date", "", null))
        assertEquals("", upDatetimeInputLabel("date", "", ""))
        assertEquals("", upDatetimeInputLabel("date", "", "   "))
        // uview treats a zero timestamp as "nothing selected yet".
        assertEquals("", upDatetimeInputLabel("date", "", 0))
        // Text that is not a timestamp at all still reaches the trigger untouched.
        assertEquals("下周", upDatetimeInputLabel("date", "", "下周"))
    }

    @Test
    fun datetimeDefaultFormatsMatchUpstream() {
        assertEquals("YYYY-MM-DD", upDatetimeDefaultFormat("date"))
        assertEquals("YYYY-MM", upDatetimeDefaultFormat("year-month"))
        assertEquals("YYYY-MM-DD HH", upDatetimeDefaultFormat("datehour"))
        assertEquals("YYYY-MM-DD HH:mm:ss", upDatetimeDefaultFormat("datetimesecond"))
        assertEquals("HH:mm", upDatetimeDefaultFormat("time"))
        assertEquals("HH:mm:ss", upDatetimeDefaultFormat("timesecond"))
        assertEquals("YYYY-MM-DD HH:mm", upDatetimeDefaultFormat("datetime"))
    }

    @Test
    fun dayjsTokensAreLoweredForSimpleDateFormat() {
        assertEquals("yyyy-MM-dd HH:mm:ss", upJavaDateFormat("YYYY-MM-DD HH:mm:ss"))
        assertEquals("yy/MM/dd", upJavaDateFormat("YY/MM/DD"))
        assertEquals("yyyy年MM月d日", upJavaDateFormat("YYYY年MM月D日"))
        assertEquals("HH:mm", upJavaDateFormat("HH:mm"))
    }

    @Test
    fun inputPropsMirrorThePickerFieldsBeforeOverrides() {
        val props = upPickerInputProps(
            label = "北京",
            inputBorder = true,
            placeholder = "请选择城市",
            disabled = true,
            disabledColor = "#f5f5f5",
            inputProps = emptyMap<String, UPRawValue>(),
            diagnostics = diagnostics,
            component = "UPPicker",
        )
        assertEquals("北京", props.modelValue)
        assertEquals("北京", props.value)
        assertTrue(props.readonly)
        assertEquals("surround", props.border)
        assertEquals("请选择城市", props.placeholder)
        assertTrue(props.disabled)
        assertEquals("#f5f5f5", props.disabledColor)
        assertTrue(events.isEmpty())
    }

    @Test
    fun inputPropsLetTheOverrideMapWinKeyByKey() {
        val props = upPickerInputProps(
            label = "北京",
            inputBorder = true,
            placeholder = "请选择城市",
            disabled = false,
            disabledColor = "",
            inputProps = mapOf(
                "border" to "bottom",
                "placeholder" to "选择出发地",
                "disabled" to true,
                "disabledColor" to "#eeeeee",
                "color" to "#303133",
                "fontSize" to 15,
                "inputAlign" to "right",
                "shape" to "circle",
                "clearable" to true,
                "readonly" to false,
                "prefixIcon" to "map",
                "suffixIcon" to "arrow-down",
                "maxlength" to 20,
                "type" to "text",
                "password" to true,
                "cursorColor" to "#2979ff",
                "customStyle" to mapOf("width" to "200px"),
                "placeholderStyle" to "color: #c0c4cc",
            ),
            diagnostics = diagnostics,
            component = "UPPicker",
        )
        assertEquals("bottom", props.border)
        assertEquals("选择出发地", props.placeholder)
        assertTrue(props.disabled)
        assertEquals("#eeeeee", props.disabledColor)
        assertEquals("#303133", props.color)
        assertEquals(15, props.fontSize)
        assertEquals("right", props.inputAlign)
        assertEquals("circle", props.shape)
        assertTrue(props.clearable)
        assertFalse(props.readonly)
        assertEquals("map", props.prefixIcon)
        assertEquals("arrow-down", props.suffixIcon)
        assertEquals(20, props.maxlength)
        assertEquals("text", props.type)
        assertTrue(props.password)
        assertEquals("#2979ff", props.cursorColor)
        assertEquals(mapOf("width" to "200px"), props.customStyle)
        assertEquals("color: #c0c4cc", props.placeholderStyle)
        assertTrue(events.isEmpty())
    }

    @Test
    fun inputPropOverrideKeysToleratePlatformCasing() {
        val props = upPickerInputProps(
            label = "",
            inputBorder = "surround",
            placeholder = "",
            disabled = false,
            disabledColor = "",
            inputProps = mapOf("input-align" to "center", "prefix_icon" to "search"),
            diagnostics = diagnostics,
            component = "UPPicker",
        )
        assertEquals("center", props.inputAlign)
        assertEquals("search", props.prefixIcon)
        assertTrue(events.isEmpty())
    }

    @Test
    fun unsupportedInputOverridesAreReportedAndDropped() {
        val props = upPickerInputProps(
            label = "",
            inputBorder = "surround",
            placeholder = "",
            disabled = false,
            disabledColor = "",
            inputProps = mapOf("focus" to true, "confirmType" to "search"),
            diagnostics = diagnostics,
            component = "UPDatetimePicker",
        )
        assertFalse(props.focus)
        assertEquals(listOf("inputProps.focus", "inputProps.confirmType"), events.map { it.property })
        assertTrue(events.all { it.component == "UPDatetimePicker" })
        assertTrue(events.all { it.reason.contains("ignored") })
    }

    @Test
    fun inputBorderAcceptsBooleansAndTheUInputEnum() {
        assertEquals("surround", upPickerInputBorder(true, diagnostics, "UPPicker"))
        assertEquals("none", upPickerInputBorder(false, diagnostics, "UPPicker"))
        assertEquals("bottom", upPickerInputBorder("bottom", diagnostics, "UPPicker"))
        assertEquals("none", upPickerInputBorder(" NONE ", diagnostics, "UPPicker"))
        assertEquals("surround", upPickerInputBorder(null, diagnostics, "UPPicker"))
        assertTrue(events.isEmpty())

        assertEquals("surround", upPickerInputBorder("dashed", diagnostics, "UPPicker"))
        assertEquals(listOf("inputBorder"), events.map { it.property })
    }

    @Test
    fun popupModeFallsBackToBottomForUnknownValues() {
        assertEquals("center", upPickerPopupMode("center", diagnostics, "UPPicker"))
        assertEquals("left", upPickerPopupMode(" LEFT ", diagnostics, "UPPicker"))
        assertTrue(events.isEmpty())

        assertEquals("bottom", upPickerPopupMode("sheet", diagnostics, "UPPicker"))
        assertEquals(listOf("popupMode"), events.map { it.property })
        assertEquals(UPPickerPopupModes, setOf("top", "bottom", "left", "right", "center"))
    }

    @Test
    fun toolbarRightSlotReplacesTheConfirmLabel() {
        assertTrue(upPickerToolbarShowsConfirm(false))
        assertFalse(upPickerToolbarShowsConfirm(true))
    }

    @Test
    fun maskStyleCountsAsDeclaredOnlyWhenItCarriesSomething() {
        assertFalse(upPickerMaskStyleDeclared(null))
        assertFalse(upPickerMaskStyleDeclared(""))
        assertFalse(upPickerMaskStyleDeclared("   "))
        assertFalse(upPickerMaskStyleDeclared(emptyMap<String, UPRawValue>()))
        assertFalse(upPickerMaskStyleDeclared(emptyList<UPRawValue>()))
        assertTrue(upPickerMaskStyleDeclared("background-color: rgba(0, 0, 0, 0.4)"))
        assertTrue(upPickerMaskStyleDeclared(mapOf("backgroundColor" to "#00000066")))
        assertTrue(upPickerMaskStyleDeclared(listOf(mapOf("opacity" to 0.4))))
    }

    @Test
    fun uInputBorderEnumStaysAlignedWithTheInputComponent() {
        assertEquals(setOf("surround", "bottom", "none"), UPPickerInputBorders)
    }

    private fun localMillis(year: Int, month: Int, day: Int, hour: Int, minute: Int, second: Int): Long =
        Calendar.getInstance().apply {
            clear()
            set(year, month - 1, day, hour, minute, second)
        }.timeInMillis
}
