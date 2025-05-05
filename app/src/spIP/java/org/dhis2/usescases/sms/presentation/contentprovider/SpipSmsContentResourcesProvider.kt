package org.dhis2.usescases.sms.presentation.contentprovider

import android.annotation.SuppressLint
import android.content.Context
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import org.dhis2.R

class SpipSmsContentResourcesProvider(
  private var context: Context
) {

  fun onSmsSentSuccessfully() = provideStringMessage(R.string.send_sms)
  fun onSmsSentEnSuccessfully() = provideStringMessage(R.string.sent_sms_successfully)
  fun onSmsSentError() = provideStringMessage(R.string.sent_sms_using_en_successfully)
  fun onSmsSentGenericError() = provideStringMessage(R.string.sent_sms_template_error)

  @SuppressLint("ResourceType") fun getOnMessageBackground(
    isSuccess: Boolean
  ) =
    if (isSuccess) provideColorMessage(R.color.colorPrimaryDark_2e7) else provideColorMessage(R.color.colorPrimaryDarkRed)

  private fun provideStringMessage(@StringRes message: Int): String {
    return context.getString(message)
  }

  @SuppressLint("ResourceType")
  private fun provideColorMessage(@StringRes color: Int): Int {
    return ContextCompat.getColor(context, color)
  }
}