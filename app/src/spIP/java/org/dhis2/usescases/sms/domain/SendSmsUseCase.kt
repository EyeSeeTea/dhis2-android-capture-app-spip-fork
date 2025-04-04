package org.dhis2.usescases.sms.domain

import org.dhis2.usescases.sms.domain.message.Message
import org.dhis2.usescases.sms.domain.message.MessageTemplateRepository
import org.dhis2.usescases.sms.domain.message.SmsRepository
import org.dhis2.usescases.sms.domain.patient.PatientRepository

class SendSmsUseCase(
    private val patientRepository: PatientRepository,
    private val smsTemplateRepository: MessageTemplateRepository,
    private val smsRepository: SmsRepository
) {
    fun invoke(uid: String) {
        val patient = patientRepository.getByUid(uid)

        val messageTemplate = smsTemplateRepository.getByLanguage(patient.preferredLanguage)

        val message = Message(
            text = messageTemplate.text
                .replace("{{fullName}}", patient.name)
                .replace("{{patientNumber}}", patient.number),
            recipients = listOf(cleanupPhoneNumber(patient.phone))
        )

        smsRepository.send(message)
    }

    private fun cleanupPhoneNumber(phoneNumber: String): String {
        return phoneNumber.replace(Regex("\\D"), "")
    }
}