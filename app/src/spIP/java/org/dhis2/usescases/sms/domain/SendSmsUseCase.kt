package org.dhis2.usescases.sms.domain

import org.dhis2.usescases.sms.domain.message.Message
import org.dhis2.usescases.sms.domain.message.MessageTemplate
import org.dhis2.usescases.sms.domain.message.MessageTemplateRepository
import org.dhis2.usescases.sms.domain.message.SmsRepository
import org.dhis2.usescases.sms.domain.patient.PatientRepository
import org.dhis2.usescases.sms.domain.patient.PreferredLanguageRepository

sealed class SmsResult{
    data object Success:SmsResult()
    data class SuccessUsingEn(val preferredLanguage: String):SmsResult()
    data object TemplateFailure:SmsResult()
    data object SendFailure:SmsResult()
}

class SendSmsUseCase(
    private val patientRepository: PatientRepository,
    private val smsTemplateRepository: MessageTemplateRepository,
    private val preferredLanguageRepository: PreferredLanguageRepository,
    private val smsRepository: SmsRepository
) {
    fun invoke(uid: String):SmsResult{
        val patient = patientRepository.getByUid(uid)

        val messageTemplate = getMessageTemplate(patient.preferredLanguage)
            ?: return SmsResult.TemplateFailure

        val message = Message(
            text = messageTemplate.text
                .replace("{{fullName}}", patient.name)
                .replace("{{patientNumber}}", patient.number),
            recipients = listOf(cleanupPhoneNumber(patient.phone))
        )

        try {
            smsRepository.send(message)

            return if (patient.preferredLanguage != "en" && messageTemplate.language == "en") {
                val language = preferredLanguageRepository.getByCode(patient.preferredLanguage)

                SmsResult.SuccessUsingEn(language.name)
            } else {
                SmsResult.Success
            }
        } catch (e: Exception) {
            return SmsResult.SendFailure
        }

    }

    private fun getMessageTemplate(language: String): MessageTemplate? {
        val messageTemplate = smsTemplateRepository.getByLanguage(language)

        return if (messageTemplate.isSome()) {
            messageTemplate.getOrThrow()
        } else {
            val defaultMessageTemplate = smsTemplateRepository.getByLanguage("en")

            if (defaultMessageTemplate.isSome()) {
                defaultMessageTemplate.getOrThrow()
            } else {
                return null
            }
        }
    }

    private fun cleanupPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("\\D"), "")
    }
}