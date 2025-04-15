package org.dhis2.usescases.teiDashboard

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.dhis2.R
import org.dhis2.usescases.sms.di.ServiceLocator
import org.dhis2.usescases.sms.domain.SmsResult


fun customClick(
    itemId: Int,
    teiDashboardMobileActivity: TeiDashboardMobileActivity,
    programUid: String,
    enrollmentUid: String,
    teiUid: String
) {
    when (itemId) {
        R.id.sendSMS -> {
            sendSms(
                teiDashboardMobileActivity,
                teiDashboardMobileActivity.binding.root,
                teiUid
            )
        }
    }
}

fun sendSms(activity: AppCompatActivity, parentView: View, teiUid: String) {
    activity.lifecycleScope.launch(Dispatchers.IO) {
        when (val result = ServiceLocator.sms().invoke(teiUid)) {
            is SmsResult.Success -> {
                showCustomSnackbar(
                    activity,
                    parentView,
                    activity.getString(R.string.sent_sms_successfully),
                    true
                )
            }

            is SmsResult.SuccessUsingEn -> {
                showCustomSnackbar(
                    activity,
                    parentView,
                    activity.getString(
                        R.string.sent_sms_using_en_successfully,
                        result.preferredLanguage
                    ),
                    true
                )
            }

            is SmsResult.TemplateFailure -> {
                showCustomSnackbar(
                    activity,
                    parentView,
                    activity.getString(R.string.sent_sms_template_error),
                    false
                )
            }

            is SmsResult.SendFailure -> {
                showCustomSnackbar(
                    activity,
                    parentView,
                    activity.getString(R.string.sent_sms_error),
                    false
                )
            }
        }
    }
}

fun showCustomSnackbar(
    activity: AppCompatActivity,
    parentView: View,
    message: String,
    isSuccess: Boolean
) {
    val snackbar = Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT)

    val color =
        if (isSuccess) ContextCompat.getColor(activity, R.color.colorPrimaryDark_2e7) // Verde
        else ContextCompat.getColor(activity, R.color.colorPrimaryDarkRed) // Rojo

    snackbar.setBackgroundTint(color)

    snackbar.show()
}