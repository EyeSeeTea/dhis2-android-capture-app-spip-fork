package org.dhis2.usescases.sms.di

import org.dhis2.usescases.sms.data.MessageTemplateD2Repository
import org.dhis2.usescases.sms.data.PatientD2Repository
import org.dhis2.usescases.sms.data.PreferredLanguageD2Repository
import org.dhis2.usescases.sms.data.SmsApiRepository
import org.dhis2.usescases.sms.data.api.ConstantApi
import org.dhis2.usescases.sms.data.api.OutboundApi
import org.dhis2.usescases.sms.domain.SendSmsUseCase
import org.hisp.dhis.android.core.D2Manager

object ServiceLocator {
    fun sms(): SendSmsUseCase {
        val d2 = D2Manager.getD2()

        val constantApi = d2.retrofit().create(
            ConstantApi::class.java
        )

        val outboundApi = d2.retrofit().create(
            OutboundApi::class.java
        )

        val patientRepository = PatientD2Repository(d2)
        val messageTemplate = MessageTemplateD2Repository(d2, constantApi)
        val preferredLanguageRepository = PreferredLanguageD2Repository(d2)
        val smsRepository = SmsApiRepository(outboundApi)

        return SendSmsUseCase(
            patientRepository,
            messageTemplate,
            preferredLanguageRepository,
            smsRepository
        )
    }
}