package net.lingyun.ultraui.android.components

import net.lingyun.ultraui.android.core.UPConfig
import net.lingyun.ultraui.android.core.UPRawValue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class UPFormPropsTest {
    @Test
    fun formPropsMirrorUpstreamDefaults() {
        val props = UPFormProps()
        assertEquals(emptyMap<String, UPRawValue>(), props.model)
        assertEquals(emptyMap<String, List<UPFormRule>>(), props.rules)
        assertEquals("message", props.errorType)
        assertTrue(props.borderBottom)
        assertEquals("left", props.labelPosition)
        assertEquals(45, props.labelWidth)
        assertEquals("left", props.labelAlign)
        assertEquals(emptyMap<String, UPRawValue>(), props.labelStyle)
    }

    @Test
    fun formItemPropsMirrorUpstreamDefaults() {
        val props = UPFormItemProps()
        assertEquals("", props.label)
        assertEquals("", props.prop)
        assertEquals(emptyList<UPFormRule>(), props.rules)
        assertEquals("", props.borderBottom)
        assertEquals("", props.labelPosition)
        assertEquals("", props.labelWidth)
        assertEquals("", props.rightIcon)
        assertEquals("", props.leftIcon)
        assertFalse(props.required)
        assertEquals("", props.leftIconStyle)
    }

    @Test
    fun formDefaultsAreSourcedFromUpConfig() {
        assertEquals("message", UPConfig.form.errorType)
        assertTrue(UPConfig.form.borderBottom)
        assertEquals(45, UPConfig.form.labelWidth)
        assertEquals("left", UPConfig.form.labelAlign)
        assertEquals("", UPConfig.formItem.borderBottom)
        assertEquals("", UPConfig.formItem.labelWidth)
        assertFalse(UPConfig.formItem.required)
    }

    @Test
    fun getPropertyWalksDottedPathsLikeUpstream() {
        val model = mapOf<String, UPRawValue>(
            "name" to "uview",
            "userInfo" to mapOf<String, UPRawValue>(
                "name" to "plus",
                "address" to mapOf<String, UPRawValue>("city" to "hangzhou"),
            ),
        )
        assertEquals("uview", upFormGetProperty(model, "name"))
        assertEquals("plus", upFormGetProperty(model, "userInfo.name"))
        assertEquals("hangzhou", upFormGetProperty(model, "userInfo.address.city"))
        assertNull(upFormGetProperty(model, "userInfo.missing"))
        assertNull(upFormGetProperty(model, "missing.deep.path"))
        assertEquals("", upFormGetProperty("not-an-object", "name"))
        assertEquals("", upFormGetProperty(null, "name"))
        assertEquals("", upFormGetProperty(model, ""))
    }

    @Test
    fun setPropertyCreatesMissingIntermediateObjects() {
        val updated = upFormSetProperty(emptyMap(), "userInfo.address.city", "ningbo")
        assertEquals("ningbo", upFormGetProperty(updated, "userInfo.address.city"))

        val model = mapOf<String, UPRawValue>("name" to "uview", "userInfo" to mapOf<String, UPRawValue>("age" to 18))
        val next = upFormSetProperty(model, "userInfo.age", 20)
        assertEquals(20, upFormGetProperty(next, "userInfo.age"))
        assertEquals("uview", upFormGetProperty(next, "name"))
        assertEquals(18, upFormGetProperty(model, "userInfo.age"))

        val replaced = upFormSetProperty(mapOf("userInfo" to "plain"), "userInfo.age", 1)
        assertEquals(1, upFormGetProperty(replaced, "userInfo.age"))
    }

    @Test
    fun formatMirrorsAsyncValidatorPlaceholders() {
        assertEquals("name is required", upFormFormat("%s is required", "name"))
        assertEquals(
            "name must be between 2 and 4 characters",
            upFormFormat("%s must be between %s and %s characters", "name", 2, 4),
        )
        assertEquals("100%", upFormFormat("%d%%", 100))
        assertEquals("a b", upFormFormat("%s", "a", "b"))
        assertEquals("%s", upFormFormat("%s"))
    }

    @Test
    fun requiredOnlyRuleAcceptsNonStringValues() {
        assertEquals(
            listOf("name is required"),
            upFormRunRules("name", "", listOf(UPFormRule(required = true))).map { it.message },
        )
        assertEquals(emptyList<String>(), upFormRunRules("name", 0, listOf(UPFormRule(required = true))).map { it.message })
        assertEquals(
            listOf("name is required"),
            upFormRunRules("name", emptyList<UPRawValue>(), listOf(UPFormRule(required = true))).map { it.message },
        )
        assertEquals(emptyList<String>(), upFormRunRules("name", false, listOf(UPFormRule(required = true))).map { it.message })
        assertEquals(
            listOf("name is required"),
            upFormRunRules("name", null, listOf(UPFormRule(required = true))).map { it.message },
        )
    }

    @Test
    fun ruleMessageOverridesGeneratedText() {
        val errors = upFormRunRules("name", "", listOf(UPFormRule(required = true, message = "请输入姓名")))
        assertEquals(listOf("请输入姓名"), errors.map { it.message })
        assertEquals("name", errors.first().field)
    }

    @Test
    fun triggerFiltersEventDrivenValidation() {
        val rules = listOf(UPFormRule(required = true, message = "必填", trigger = listOf("blur")))
        assertEquals(emptyList<String>(), upFormRunRules("name", "", rules, event = "change").map { it.message })
        assertEquals(listOf("必填"), upFormRunRules("name", "", rules, event = "blur").map { it.message })
        assertEquals(listOf("必填"), upFormRunRules("name", "", rules, event = null).map { it.message })
        assertEquals(
            emptyList<String>(),
            upFormRunRules("name", "", listOf(UPFormRule(required = true)), event = "blur").map { it.message },
        )
    }

    @Test
    fun stringRangeMessagesMatchUpstream() {
        assertEquals(
            listOf("name must be at least 6 characters"),
            upFormRunRules("name", "abc", listOf(UPFormRule(min = 6))).map { it.message },
        )
        assertEquals(
            listOf("name cannot be longer than 3 characters"),
            upFormRunRules("name", "abcdef", listOf(UPFormRule(max = 3))).map { it.message },
        )
        assertEquals(
            listOf("name must be exactly 4 characters"),
            upFormRunRules("name", "abc", listOf(UPFormRule(len = 4))).map { it.message },
        )
        assertEquals(
            listOf("name must be between 2 and 4 characters"),
            upFormRunRules("name", "abcdef", listOf(UPFormRule(min = 2, max = 4))).map { it.message },
        )
        assertEquals(emptyList<String>(), upFormRunRules("name", "", listOf(UPFormRule(min = 6))).map { it.message })
    }

    @Test
    fun patternRuleReportsValueAndPattern() {
        assertEquals(
            listOf("code value abc does not match pattern /^\\d+$/"),
            upFormRunRules("code", "abc", listOf(UPFormRule(pattern = Regex("^\\d+$")))).map { it.message },
        )
        assertEquals(emptyList<String>(), upFormRunRules("code", "123", listOf(UPFormRule(pattern = Regex("^\\d+$")))).map { it.message })
        assertEquals(
            listOf("code value abc does not match pattern ^\\d+$"),
            upFormRunRules("code", "abc", listOf(UPFormRule(type = "pattern", pattern = "^\\d+$"))).map { it.message },
        )
    }

    @Test
    fun whitespaceRuleRejectsBlankStrings() {
        assertEquals(
            listOf("name cannot be empty"),
            upFormRunRules("name", "   ", listOf(UPFormRule(required = true, whitespace = true))).map { it.message },
        )
        assertEquals(
            emptyList<String>(),
            upFormRunRules("name", " a ", listOf(UPFormRule(required = true, whitespace = true))).map { it.message },
        )
    }

    @Test
    fun typeValidatorsMirrorUpstreamCoercion() {
        assertEquals(
            listOf("age is not a number"),
            upFormRunRules("age", "abc", listOf(UPFormRule(type = "number"))).map { it.message },
        )
        assertEquals(emptyList<String>(), upFormRunRules("age", "123", listOf(UPFormRule(type = "number"))).map { it.message })
        assertEquals(
            listOf("age is not an integer"),
            upFormRunRules("age", "1.5", listOf(UPFormRule(type = "integer"))).map { it.message },
        )
        assertEquals(
            listOf("mail is not a valid email"),
            upFormRunRules("mail", "a@b", listOf(UPFormRule(type = "email"))).map { it.message },
        )
        assertEquals(emptyList<String>(), upFormRunRules("mail", "a@b.com", listOf(UPFormRule(type = "email"))).map { it.message })
        assertEquals(
            listOf("hobby is not an array"),
            upFormRunRules("hobby", "x", listOf(UPFormRule(type = "array"))).map { it.message },
        )
        assertEquals(
            listOf("hobby cannot be less than 2 in length"),
            upFormRunRules("hobby", listOf<UPRawValue>("a"), listOf(UPFormRule(type = "array", min = 2))).map { it.message },
        )
    }

    @Test
    fun enumRuleRequiresExplicitType() {
        assertEquals(
            listOf("sex must be one of 男, 女"),
            upFormRunRules("sex", "未知", listOf(UPFormRule(type = "enum", enum = listOf("男", "女")))).map { it.message },
        )
        assertEquals(
            emptyList<String>(),
            upFormRunRules("sex", "未知", listOf(UPFormRule(enum = listOf("男", "女")))).map { it.message },
        )
    }

    @Test
    fun stringValidatorAccumulatesTypeAndRangeErrors() {
        assertEquals(
            listOf("age is not a string", "age cannot be less than 1"),
            upFormRunRules("age", 0, listOf(UPFormRule(required = true, min = 1))).map { it.message },
        )
    }

    @Test
    fun customValidatorSupportsUpstreamReturnShapes() {
        assertEquals(
            emptyList<String>(),
            upFormRunRules("name", "ok", listOf(UPFormRule(validator = { _, _ -> true }))).map { it.message },
        )
        assertEquals(
            listOf("name fails"),
            upFormRunRules("name", "no", listOf(UPFormRule(validator = { _, _ -> false }))).map { it.message },
        )
        assertEquals(
            listOf("自定义失败"),
            upFormRunRules("name", "no", listOf(UPFormRule(message = "自定义失败", validator = { _, _ -> false }))).map { it.message },
        )
        assertEquals(
            listOf("手机号不合法"),
            upFormRunRules("phone", "123", listOf(UPFormRule(validator = { _, value -> "手机号不合法".takeIf { value.toString().length < 11 } }))).map { it.message },
        )
        assertEquals(
            listOf("a", "b"),
            upFormRunRules("name", "x", listOf(UPFormRule(validator = { _, _ -> listOf("a", "b") }))).map { it.message },
        )
    }

    @Test
    fun transformRunsBeforeValidation() {
        val rules = listOf(UPFormRule(required = true, transform = { value -> value.toString().trim() }))
        assertEquals(listOf("name is required"), upFormRunRules("name", "  ", rules).map { it.message })
    }

    @Test
    fun errorsCarryTheOwningPropPath() {
        val errors = upFormRunRules("name", "", listOf(UPFormRule(required = true)), prop = "userInfo.name")
        assertEquals("userInfo.name", errors.first().prop)
        assertEquals("name", errors.first().field)
    }
}
