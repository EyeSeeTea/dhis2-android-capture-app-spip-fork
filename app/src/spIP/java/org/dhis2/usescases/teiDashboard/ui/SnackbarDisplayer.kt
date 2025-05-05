package org.dhis2.usescases.teiDashboard.ui

import android.view.View

interface SnackbarDisplayer {
  suspend fun showCustomSnackBar(
    message: String,
    isSuccess: Boolean,
    parentView: View,
  )
}