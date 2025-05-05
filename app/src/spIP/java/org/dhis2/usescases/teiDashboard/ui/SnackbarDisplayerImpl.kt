package org.dhis2.usescases.teiDashboard.ui

import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import org.dhis2.R
import org.dhis2.usescases.sms.presentation.contentprovider.SpipSmsContentResourcesProvider

class SnackbarDisplayerImpl(
  private val contentResourcesProvider: SpipSmsContentResourcesProvider,
) : SnackbarDisplayer {

  override suspend fun showCustomSnackBar(message: String, isSuccess: Boolean, parentView: View) {
    Snackbar.make(parentView, message, Snackbar.LENGTH_SHORT).apply {
      val color = contentResourcesProvider.getOnMessageBackground(isSuccess)
      setBackgroundTint(color)
      view.findViewById<TextView>(R.id.snackbar_text)?.apply {
        maxLines = Int.MAX_VALUE
      }
    }.show()
  }
}