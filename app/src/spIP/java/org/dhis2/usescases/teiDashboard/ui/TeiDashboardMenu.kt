package org.dhis2.usescases.teiDashboard.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.LockReset
import androidx.compose.material.icons.outlined.MoveDown
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.Sync
import androidx.compose.material.icons.outlined.Timeline
import androidx.compose.material.icons.outlined.Workspaces
import org.dhis2.R
import org.dhis2.commons.resources.ResourceManager
import org.dhis2.usescases.teiDashboard.DashboardEnrollmentModel
import org.dhis2.usescases.teiDashboard.DashboardViewModel
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.ACTIVATE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.COMPLETE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.DEACTIVATE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.DELETE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.ENROLLMENTS
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.FOLLOW_UP
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.GROUP_BY_STAGE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.HELP
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.REMOVE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.SEND_SMS
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.SHARE
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.SYNC
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.TRANSFER
import org.dhis2.usescases.teiDashboard.EnrollmentMenuItem.VIEW_TIMELINE
import org.dhis2.usescases.teiDashboard.TeiDashboardContracts.Presenter
import org.hisp.dhis.android.core.enrollment.EnrollmentStatus
import org.hisp.dhis.mobile.ui.designsystem.component.menu.MenuItemData
import org.hisp.dhis.mobile.ui.designsystem.component.menu.MenuItemStyle
import org.hisp.dhis.mobile.ui.designsystem.component.menu.MenuLeadingElement
import org.hisp.dhis.mobile.ui.designsystem.theme.SurfaceColor
import org.hisp.dhis.mobile.ui.designsystem.theme.TextColor

fun getEnrollmentMenuList(
    enrollmentUid: String?,
    resourceManager: ResourceManager,
    presenter: Presenter,
    dashboardViewModel: DashboardViewModel,
): List<MenuItemData<EnrollmentMenuItem>> {
    return if (enrollmentUid == null) {
        buildMenuForNoEnrollment(resourceManager, presenter)
    } else {
        buildMenuForEnrollment(enrollmentUid, resourceManager, presenter, dashboardViewModel)
    }
}

private fun buildMenuForNoEnrollment(
    resourceManager: ResourceManager,
    presenter: Presenter,
): List<MenuItemData<EnrollmentMenuItem>> {
    return buildList {
        addSyncMenuItem(resourceManager)
        addMoreEnrollmentsMenuItem(resourceManager)
        addDeleteTeiMenuItem(presenter, resourceManager)
    }
}

private fun buildMenuForEnrollment(
    enrollmentUid: String,
    resourceManager: ResourceManager,
    presenter: Presenter,
    dashboardViewModel: DashboardViewModel,
): List<MenuItemData<EnrollmentMenuItem>> {
    return buildList {
        if (presenter.isCmoProgram) {
            addSendSmsMenuItem(resourceManager) // New Menu Item
        }
        addSyncMenuItem(resourceManager)
        addIfTeiCanBeTransferred(dashboardViewModel, resourceManager)
        addFollowUpMenuItem(dashboardViewModel, resourceManager)
        addTimelineOrGroupByStageMenuItem(dashboardViewModel, resourceManager)
        addHelpMenuItem(resourceManager)
        addMoreEnrollmentsMenuItem(resourceManager)
        addShareMenuItem(resourceManager)
        addStatusMenuItems(enrollmentUid, resourceManager, presenter)
        addRemoveEnrollmentItem(enrollmentUid, resourceManager, presenter, dashboardViewModel)
        addDeleteTeiMenuItem(presenter, resourceManager)
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addSyncMenuItem(
    resourceManager: ResourceManager,
) {
    add(
        MenuItemData(
            id = SYNC,
            label = resourceManager.getString(R.string.refresh_this_record),
            leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.Sync),
        ),
    )
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addIfTeiCanBeTransferred(
    dashboardViewModel: DashboardViewModel,
    resourceManager: ResourceManager,
) {
    if (dashboardViewModel.checkIfTeiCanBeTransferred()) {
        add(
            MenuItemData(
                id = TRANSFER,
                label = resourceManager.getString(R.string.transfer),
                leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.MoveDown),
            ),
        )
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addFollowUpMenuItem(
    dashboardViewModel: DashboardViewModel,
    resourceManager: ResourceManager,
) {
    if (!dashboardViewModel.showFollowUpBar.value) {
        add(
            MenuItemData(
                id = FOLLOW_UP,
                label = resourceManager.getString(R.string.mark_follow_up),
                leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.Flag),
            ),
        )
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addTimelineOrGroupByStageMenuItem(
    dashboardViewModel: DashboardViewModel,
    resourceManager: ResourceManager,
) {
    if (dashboardViewModel.groupByStage.value != false) {
        add(
            MenuItemData(
                id = VIEW_TIMELINE,
                label = resourceManager.getString(R.string.view_timeline),
                leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.Timeline),
            ),
        )
    } else {
        add(
            MenuItemData(
                id = GROUP_BY_STAGE,
                label = resourceManager.getString(R.string.group_by_stage),
                leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.Workspaces),
            ),
        )
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addHelpMenuItem(
    resourceManager: ResourceManager,
) {
    add(
        MenuItemData(
            id = HELP,
            label = resourceManager.getString(R.string.showHelp),
            leadingElement = MenuLeadingElement.Icon(icon = Icons.AutoMirrored.Outlined.HelpOutline),
        ),
    )
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addMoreEnrollmentsMenuItem(
    resourceManager: ResourceManager,
) {
    add(
        MenuItemData(
            id = ENROLLMENTS,
            label = resourceManager.getString(R.string.more_enrollments),
            leadingElement = MenuLeadingElement.Icon(icon = Icons.AutoMirrored.Outlined.Assignment),
        ),
    )
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addShareMenuItem(
    resourceManager: ResourceManager,
) {
    add(
        MenuItemData(
            id = SHARE,
            label = resourceManager.getString(R.string.share),
            showDivider = true,
            leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.Share),
        ),
    )
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addStatusMenuItems(
    enrollmentUid: String,
    resourceManager: ResourceManager,
    presenter: Presenter,
) {
    val status = presenter.getEnrollmentStatus(enrollmentUid)
    if (status != EnrollmentStatus.COMPLETED) {
        add(
            MenuItemData(
                id = COMPLETE,
                label = resourceManager.getString(R.string.complete),
                leadingElement = MenuLeadingElement.Icon(
                    icon = Icons.Outlined.CheckCircle,
                    defaultTintColor = SurfaceColor.CustomGreen,
                    selectedTintColor = SurfaceColor.CustomGreen,
                ),
            ),
        )
    }

    if (status != EnrollmentStatus.ACTIVE) {
        add(
            MenuItemData(
                id = ACTIVATE,
                label = resourceManager.getString(R.string.re_open),
                showDivider = status == EnrollmentStatus.CANCELLED,
                leadingElement = MenuLeadingElement.Icon(
                    icon = Icons.Outlined.LockReset,
                    defaultTintColor = SurfaceColor.Warning,
                    selectedTintColor = SurfaceColor.Warning,
                ),
            ),
        )
    }

    if (status != EnrollmentStatus.CANCELLED) {
        add(
            MenuItemData(
                id = DEACTIVATE,
                label = resourceManager.getString(R.string.deactivate),
                showDivider = true,
                leadingElement = MenuLeadingElement.Icon(
                    icon = Icons.Outlined.Cancel,
                    defaultTintColor = TextColor.OnDisabledSurface,
                    selectedTintColor = TextColor.OnDisabledSurface,
                ),
            ),
        )
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addRemoveEnrollmentItem(
    enrollmentUid: String,
    resourceManager: ResourceManager,
    presenter: Presenter,
    dashboardViewModel: DashboardViewModel,
) {
    if (presenter.checkIfEnrollmentCanBeDeleted(enrollmentUid)) {
        val dashboardModel = dashboardViewModel.dashboardModel.value
        val programmeName = if (dashboardModel is DashboardEnrollmentModel) {
            dashboardModel.currentProgram().displayName()
        } else {
            ""
        }
        add(
            MenuItemData(
                id = REMOVE,
                label = resourceManager.getString(R.string.remove_from),
                supportingText = programmeName,
                style = MenuItemStyle.ALERT,
                leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.DeleteOutline),
            ),
        )
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addDeleteTeiMenuItem(
    presenter: Presenter,
    resourceManager: ResourceManager,
) {
    if (presenter.checkIfTEICanBeDeleted()) {
        add(
            MenuItemData(
                id = DELETE,
                label = resourceManager.getString(R.string.dashboard_menu_delete_tei_v2, presenter.teType),
                style = MenuItemStyle.ALERT,
                leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.DeleteForever),
            ),
        )
    }
}

private fun MutableList<MenuItemData<EnrollmentMenuItem>>.addSendSmsMenuItem(
    resourceManager: ResourceManager
) {
    add(
        MenuItemData(
            id = SEND_SMS,
            label = resourceManager.getString(R.string.send_sms),
            leadingElement = MenuLeadingElement.Icon(icon = Icons.Outlined.Sms),
        ),
    )
}