package net.lingyun.ultraui.android

import net.lingyun.ultraui.android.core.UPConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ProjectContractTest {
    @Test
    fun libraryUsesThePublishedRootPackage() {
        assertEquals("net.lingyun.ultraui.android.core", UPConfig::class.java.packageName)
    }

    @Test
    fun everyRequestedComponentExposesAPropsClassInThePublicPackage() {
        val expectedPropsClasses = listOf(
            "UPButtonProps",
            "UPTagProps",
            "UPBadgeProps",
            "UPDividerProps",
            "UPGapProps",
            "UPLineProps",
            "UPLinkProps",
            "UPTextProps",
            "UPTitleProps",
            "UPOverlayProps",
            "UPPopupProps",
            "UPModalProps",
            "UPToastProps",
            "UPCellProps",
            "UPCellGroupProps",
            "UPImageProps",
            "UPAvatarProps",
            "UPAvatarGroupProps",
            "UPEmptyProps",
            "UPLoadingPageProps",
            "UPLoadmoreProps",
            "UPInputProps",
            "UPTextareaProps",
            "UPSearchProps",
            "UPCodeInputProps",
            "UPSwitchProps",
            "UPRateProps",
            "UPNumberBoxProps",
            "UPCheckboxProps",
            "UPCheckboxGroupProps",
            "UPRadioProps",
            "UPRadioGroupProps",
            "UPRowProps",
            "UPColProps",
            "UPGridProps",
            "UPGridItemProps",
            "UPLineProgressProps",
            "UPCircleProgressProps",
            "UPAlertProps",
            "UPActionSheetProps",
            "UPNotifyProps",
            "UPBackTopProps",
            "UPCardProps",
            "UPCollapseProps",
            "UPCollapseItemProps",
            "UPDropdownProps",
            "UPDropdownItemProps",
            "UPNoticeBarProps",
            "UPNavbarProps",
            "UPNavbarMiniProps",
            "UPStatusBarProps",
            "UPSafeBottomProps",
            "UPTabsProps",
            "UPTabsItemProps",
            "UPSubsectionProps",
            "UPStepsProps",
            "UPStepsItemProps",
            "UPListProps",
            "UPListItemProps",
            "UPIndexListProps",
            "UPIndexItemProps",
            "UPIndexAnchorProps",
            "UPScrollListProps",
            "UPPopoverProps",
            "UPTooltipProps",
            "UPStickyProps",
            "UPSwipeActionProps",
            "UPSwipeActionItemProps",
            "UPSwiperProps",
            "UPSwiperIndicatorProps",
            "UPSkeletonProps",
            "UPReadMoreProps",
            "UPColumnNoticeProps",
            "UPRowNoticeProps",
            "UPCountToProps",
            "UPCountDownProps",
            "UPPickerProps",
            "UPPickerColumnProps",
            "UPPaginationProps",
            "UPSelectProps",
        )

        expectedPropsClasses.forEach { simpleName ->
            val type = runCatching {
                Class.forName("net.lingyun.ultraui.android.components.$simpleName")
            }.getOrNull()
            assertNotNull("缺少公开 Props 类: $simpleName", type)
            assertEquals(
                "net.lingyun.ultraui.android.components",
                type!!.packageName,
            )
        }
    }
}
