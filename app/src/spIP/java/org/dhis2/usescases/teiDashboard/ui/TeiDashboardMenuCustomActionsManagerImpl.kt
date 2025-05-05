package org.dhis2.usescases.teiDashboard.ui

import android.view.View
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dhis2.commons.viewmodel.DispatcherProvider
import org.dhis2.usescases.sms.domain.model.sms.SmsResult
import org.dhis2.usescases.sms.domain.usecase.SendSmsUseCase
import org.dhis2.usescases.sms.presentation.contentprovider.SpipSmsContentResourcesProvider
import org.dhis2.usescases.teiDashboard.TeiDashboardMenuCustomActionsManager

class TeiDashboardMenuCustomActionsManagerImpl(
  private val dispatcher: DispatcherProvider,
  private val sendSmsUseCase: SendSmsUseCase,
  private val contentResourcesProvider: SpipSmsContentResourcesProvider,
  private val snackbarDisplayer: SnackbarDisplayer,
) : TeiDashboardMenuCustomActionsManager, CoroutineScope {

  private val job = Job()
  override val coroutineContext = dispatcher.io() + job

  /**
   * This method is used to send SMS to the TEI.
   * @param teiUid The UID of the TEI to whom the SMS will be sent.
   */
  override fun sendSms(
    teiUid: String?,
    parentView: View
  ) {
    launch {
      val result = teiUid?.let { sendSmsUseCase.invoke(it) } ?: SmsResult.TemplateFailure
      val message = when (result) {
        is SmsResult.Success -> contentResourcesProvider.onSmsSentSuccessfully()
        is SmsResult.SuccessUsingEn -> contentResourcesProvider.onSmsSentEnSuccessfully()
        is SmsResult.TemplateFailure -> contentResourcesProvider.onSmsSentGenericError()
        is SmsResult.SendFailure -> contentResourcesProvider.onSmsSentError()
      }
      showCustomSnackBar(
        message = message,
        isSuccess = result is SmsResult.Success || result is SmsResult.SuccessUsingEn,
        parentView = parentView
      )
    }
  }

  private suspend fun showCustomSnackBar(
    message: String,
    isSuccess: Boolean,
    parentView: View,
  ) {
    withContext(dispatcher.ui()) {
      snackbarDisplayer.showCustomSnackBar(
        message = message,
        isSuccess = isSuccess,
        parentView = parentView
      )
    }
  }

  override fun onDestroy() {
    job.cancel()
  }
}