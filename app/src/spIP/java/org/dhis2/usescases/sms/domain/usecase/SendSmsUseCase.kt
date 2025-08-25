package org.dhis2.usescases.sms.domain.usecase

import org.dhis2.usescases.sms.data.model.MessageTemplate
import org.dhis2.usescases.sms.domain.model.sms.Message
import org.dhis2.usescases.sms.domain.model.sms.SmsResult
import org.dhis2.usescases.sms.domain.repository.message.MessageTemplateRepository
import org.dhis2.usescases.sms.domain.repository.patient.PatientRepository
import org.dhis2.usescases.sms.domain.repository.preferred.PreferredLanguageRepository
import org.dhis2.usescases.sms.domain.repository.sms.SmsRepository


class SendSmsUseCase(
  private val patientRepository: PatientRepository,
  private val smsTemplateRepository: MessageTemplateRepository,
  private val preferredLanguageRepository: PreferredLanguageRepository,
  private val smsRepository: SmsRepository
) {
  suspend fun invoke(uid: String):SmsResult{
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

  private suspend fun getMessageTemplate(language: String): MessageTemplate? {
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