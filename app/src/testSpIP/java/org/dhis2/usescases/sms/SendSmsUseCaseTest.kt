package org.dhis2.usescases.sms

import org.dhis2.usescases.sms.domain.SendSmsUseCase
import org.dhis2.usescases.sms.domain.message.Message
import org.dhis2.usescases.sms.domain.message.MessageTemplate
import org.dhis2.usescases.sms.domain.message.MessageTemplateRepository
import org.dhis2.usescases.sms.domain.message.SmsRepository
import org.dhis2.usescases.sms.domain.patient.Patient
import org.dhis2.usescases.sms.domain.patient.PatientRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class SendSmsUseCaseTest {
    @Mock
    lateinit var patientRepository: PatientRepository

    @Mock
    lateinit var messageTemplateRepository: MessageTemplateRepository

    @Mock
    lateinit var smsRepository: SmsRepository

    @Test
    fun `should send expected message to expected number`() {
        val useCase = givenAPatientATemplate(
            "John Doe",
            "1234567890",
            "1234567890",
            "Hello {{fullName}}, your patient number is {{patientNumber}}"
        )

        useCase.invoke("PATIENT_UID")

        val expectedMessage = Message(
            text = "Hello John Doe, your patient number is 1234567890",
            recipients = listOf("1234567890")
        )

        verify(smsRepository).send(expectedMessage)
    }

    private fun givenAPatientATemplate(
        patientName: String,
        patientNumber: String,
        phone: String,
        messageTemplate: String
    ): SendSmsUseCase {

        whenever(
            patientRepository.getByUid(any())
        ).thenReturn(
            Patient(
                "PATIENT_UID",
                patientNumber,
                patientName,
                phone,
                "en"
            )
        )

        whenever(
            messageTemplateRepository.getByLanguage(any())
        ).thenReturn(MessageTemplate(text = messageTemplate))

        return SendSmsUseCase(
            patientRepository,
            messageTemplateRepository,
            smsRepository
        )
    }
}