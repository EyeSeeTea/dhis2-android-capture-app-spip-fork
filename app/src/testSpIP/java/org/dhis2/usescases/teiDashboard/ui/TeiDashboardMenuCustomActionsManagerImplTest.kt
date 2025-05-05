package org.dhis2.usescases.teiDashboard.ui

import android.view.View
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.dhis2.commons.viewmodel.DispatcherProvider
import org.dhis2.usescases.sms.domain.model.sms.SmsResult
import org.dhis2.usescases.sms.domain.usecase.SendSmsUseCase
import org.dhis2.usescases.sms.presentation.contentprovider.SpipSmsContentResourcesProvider
import org.dhis2.usescases.teiDashboard.TeiDashboardMenuCustomActionsManager
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.verify
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.whenever

class TeiDashboardMenuCustomActionsManagerImplTest {

  private val dispatcherProvider: DispatcherProvider = mock {
    on { io() } doReturn Dispatchers.Unconfined
    on { ui() } doReturn Dispatchers.Unconfined
  }
  private var sendSmsUseCase: SendSmsUseCase = mock()
  private var contentResourcesProvider: SpipSmsContentResourcesProvider = mock {
    on { onSmsSentSuccessfully() } doReturn "SMS sent successfully"
    on { onSmsSentEnSuccessfully() } doReturn "SMS sent successfully (EN)"
    on { onSmsSentError() } doReturn "Error sending SMS"
    on { onSmsSentGenericError() } doReturn "Template error"
    on { getOnMessageBackground(true) } doReturn 0x00FF00
    on { getOnMessageBackground(false) } doReturn 0xFF0000
  }
  private var snackBarDisplayed: SnackbarDisplayer = mock()
  private var parentView: View = mock()
  private lateinit var manager: TeiDashboardMenuCustomActionsManager

  private val teiUid = "12345"

  @Before
  fun setUp() {
    manager = TeiDashboardMenuCustomActionsManagerImpl(
      dispatcherProvider,
      sendSmsUseCase,
      contentResourcesProvider,
      snackBarDisplayed,
    )
  }

  @Test
  fun `WHEN Sms Is Sent Successfully THEN show Success Message`() = runTest {
    whenever(sendSmsUseCase.invoke(teiUid)).thenReturn(SmsResult.Success)
    whenever(contentResourcesProvider.getOnMessageBackground(true)).thenReturn(0x00FF00)

    manager.sendSms(teiUid, parentView)

    verify(contentResourcesProvider).onSmsSentSuccessfully()
    verify(contentResourcesProvider, never()).onSmsSentGenericError()
  }

  @Test
  fun `WHEN Sms Template Fails THEN show Error Message`() = runTest {
    whenever(sendSmsUseCase.invoke(teiUid)).thenReturn(SmsResult.TemplateFailure)
    whenever(contentResourcesProvider.getOnMessageBackground(false)).thenReturn(0xFF0000)

    manager.sendSms(teiUid, parentView)

    verify(contentResourcesProvider).onSmsSentGenericError()
  }

  @Test
  fun `WHEN teiUid is null THEN show Template Error Message`() = runTest {
    whenever(contentResourcesProvider.getOnMessageBackground(false)).thenReturn(0xFF0000)

    manager.sendSms(null, parentView)

    verify(contentResourcesProvider).onSmsSentGenericError()
  }

  @Test
  fun `WHEN SMS sending is Fails THEN show Error Message`() = runTest {
    whenever(sendSmsUseCase.invoke(teiUid)).thenReturn(SmsResult.SendFailure)
    whenever(contentResourcesProvider.getOnMessageBackground(false)).thenReturn(0xFF0000)

    manager.sendSms(teiUid, parentView)

    verify(contentResourcesProvider).onSmsSentError()
    verify(contentResourcesProvider, never()).onSmsSentGenericError()
  }

  @Test
  fun `WHEN SMS Is Sent Successfully Using English THEN show Success Message`() = runTest {
    whenever(sendSmsUseCase.invoke(teiUid)).thenReturn(SmsResult.SuccessUsingEn("EN"))
    whenever(contentResourcesProvider.getOnMessageBackground(true)).thenReturn(0x00FF00)

    manager.sendSms(teiUid, parentView)

    verify(contentResourcesProvider).onSmsSentEnSuccessfully()
    verify(contentResourcesProvider, never()).onSmsSentGenericError()
  }
}