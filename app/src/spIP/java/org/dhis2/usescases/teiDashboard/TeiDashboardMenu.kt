package org.dhis2.usescases.teiDashboard

import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dhis2.R
import org.dhis2.usescases.sms.di.ServiceLocator


fun customClick(
    itemId: Int,
    teiDashboardMobileActivity: TeiDashboardMobileActivity,
    programUid: String,
    enrollmentUid: String,
    teiUid: String
) {
    when (itemId) {
        R.id.sendSMS -> {
            teiDashboardMobileActivity.lifecycleScope.launch(Dispatchers.IO) {
                try {
                    ServiceLocator.sms().invoke(teiUid)

                    showCustomSnackbar(
                        teiDashboardMobileActivity,
                        teiDashboardMobileActivity.getString(R.string.sent_sms_successfully),
                        true
                    )
                } catch (e: Exception) {
                    showCustomSnackbar(
                        teiDashboardMobileActivity,
                        teiDashboardMobileActivity.getString(R.string.sent_sms_error),
                        false
                    )
                }
            }
        }
    }
}

fun showCustomSnackbar(activity: TeiDashboardMobileActivity, message: String, isSuccess: Boolean) {
    val snackbar = Snackbar.make(activity.binding.root, message, Snackbar.LENGTH_SHORT)

    val color = if (isSuccess) ContextCompat.getColor(activity, R.color.colorPrimaryDark_2e7) // Verde
    else ContextCompat.getColor(activity, R.color.colorPrimaryDarkRed) // Rojo

    snackbar.setBackgroundTint(color)

    snackbar.show()
}