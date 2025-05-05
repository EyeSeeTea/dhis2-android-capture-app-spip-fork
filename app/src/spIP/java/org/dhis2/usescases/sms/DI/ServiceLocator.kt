package org.dhis2.usescases.sms.DI

import android.content.Context
import org.dhis2.usescases.di.SpipSmsDispatcherProvider
import org.dhis2.usescases.sms.data.api.ConstantApi
import org.dhis2.usescases.sms.data.api.ConstantApiImpl
import org.dhis2.usescases.sms.data.api.OutboundApi
import org.dhis2.usescases.sms.data.api.OutboundApiImpl
import org.dhis2.usescases.sms.data.repository.message.MessageTemplateD2Repository
import org.dhis2.usescases.sms.data.repository.patient.PatientD2Repository
import org.dhis2.usescases.sms.data.repository.preferred.PreferredLanguageD2Repository
import org.dhis2.usescases.sms.data.repository.sms.SmsApiRepository
import org.dhis2.usescases.sms.domain.usecase.SendSmsUseCase
import org.dhis2.usescases.sms.presentation.contentprovider.SpipSmsContentResourcesProvider
import org.dhis2.usescases.teiDashboard.TeiDashboardMenuCustomActionsManager
import org.dhis2.usescases.teiDashboard.ui.SnackbarDisplayer
import org.dhis2.usescases.teiDashboard.ui.SnackbarDisplayerImpl
import org.dhis2.usescases.teiDashboard.ui.TeiDashboardMenuCustomActionsManagerImpl
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.D2Manager
import org.hisp.dhis.android.core.arch.api.HttpServiceClient

object SPIPServiceLocator {

  private fun provideD2(): D2 {
    return D2Manager.getD2()
  }

  private fun provideHttpClient(): HttpServiceClient {
    return D2Manager.getD2().httpServiceClient()
  }

  private fun provideSendSmsUseCase(): SendSmsUseCase {

    val outboundApi: OutboundApi = OutboundApiImpl(provideHttpClient())
    val constantApi: ConstantApi = ConstantApiImpl(provideHttpClient())

    val patientRepository = PatientD2Repository(provideD2())
    val messageTemplate = MessageTemplateD2Repository(constantApi, provideD2())
    val preferredLanguageRepository = PreferredLanguageD2Repository(provideD2())
    val smsRepository = SmsApiRepository(outboundApi)

    return SendSmsUseCase(
      patientRepository,
      messageTemplate,
      preferredLanguageRepository,
      smsRepository
    )
  }

  private fun provideSpipSmsContentResourcesProvider(
    context: Context
  ): SpipSmsContentResourcesProvider {
    return SpipSmsContentResourcesProvider(context)
  }

  private fun provideSnackbarDisplayer(
    contentResourcesProvider: SpipSmsContentResourcesProvider,
  ): SnackbarDisplayer {
    return SnackbarDisplayerImpl(contentResourcesProvider)
  }

  fun provideTeiDashboardMenuCustomActionsManager(
    context: Context
  ): TeiDashboardMenuCustomActionsManager {
    return TeiDashboardMenuCustomActionsManagerImpl(
      dispatcher = provideDispatcherProvider(),
      sendSmsUseCase = provideSendSmsUseCase(),
      contentResourcesProvider = provideSpipSmsContentResourcesProvider(context),
      snackbarDisplayer = provideSnackbarDisplayer(provideSpipSmsContentResourcesProvider(context))
    )
  }

  private fun provideDispatcherProvider() = SpipSmsDispatcherProvider()

}