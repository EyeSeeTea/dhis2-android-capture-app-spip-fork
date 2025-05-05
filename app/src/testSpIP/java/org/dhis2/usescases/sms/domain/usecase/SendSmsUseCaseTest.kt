package org.dhis2.usescases.sms.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.dhis2.usescases.sms.data.model.MessageTemplate
import org.dhis2.usescases.sms.data.model.OutboundResponse
import org.dhis2.usescases.sms.domain.model.patient.Patient
import org.dhis2.usescases.sms.domain.model.preffered.PreferredLanguage
import org.dhis2.usescases.sms.domain.model.sms.Message
import org.dhis2.usescases.sms.domain.model.sms.SmsResult
import org.dhis2.usescases.sms.domain.repository.message.MessageTemplateRepository
import org.dhis2.usescases.sms.domain.repository.patient.PatientRepository
import org.dhis2.usescases.sms.domain.repository.preferred.PreferredLanguageRepository
import org.dhis2.usescases.sms.domain.repository.sms.SmsRepository
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@ExperimentalCoroutinesApi
class SendSmsUseCaseTest {

  private val patientRepository: PatientRepository = mock()
  private val smsTemplateRepository: MessageTemplateRepository = mock()
  private val preferredLanguageRepository: PreferredLanguageRepository = mock()
  private val smsRepository: SmsRepository = mock()
  private lateinit var sendSmsUseCase: SendSmsUseCase
  private val fakeOutboundResponse : OutboundResponse = mock()
  private val uid = "12345"
  private val fakeName = "John Doe"
  private val LANGUAGE_EN = "en"
  private val LANGUAGE_ES = "es"
  private val LANGUAGE_FR = "fr"

  @Before
  fun setUp() {
    sendSmsUseCase = SendSmsUseCase(
      patientRepository,
      smsTemplateRepository,
      preferredLanguageRepository,
      smsRepository
    )
  }

  @Test
  fun `WHEN Patient has preferred Language THEN send SmsSuccessfully`() = runTest {
    val patient = Patient(
      uid = uid,
      number = "123456789",
      name = fakeName,
      phone = "123456789",
      preferredLanguage = LANGUAGE_EN
    )
    val messageTemplate = MessageTemplate("Hola {{fullName}}", LANGUAGE_EN)
    val message = Message("Hola $fakeName", listOf("123456789"))

    whenever(patientRepository.getByUid(uid)).thenReturn(patient)
    whenever(smsTemplateRepository.getByLanguage(LANGUAGE_EN)).thenReturn(Result.success(messageTemplate))
    whenever(smsRepository.send(message)).thenReturn(Result.success(fakeOutboundResponse))

    val result = sendSmsUseCase.invoke(uid)

    assert(result is SmsResult.Success)
  }

  @Test
  fun `WHEN Preferred Language Template Not Found THEN sends Sms Successfully Using English`() = runTest {
    val patient = Patient(
      uid = uid,
      number = "123456789",
      name = fakeName,
      phone = "123456789",
      preferredLanguage = LANGUAGE_FR
    )
    val preferredLanguage = PreferredLanguage(uid, LANGUAGE_FR, "French")
    val messageTemplate = MessageTemplate("Hello {{fullName}}", LANGUAGE_EN)
    val message = Message("Hello $fakeName", listOf("123456789"))

    whenever(patientRepository.getByUid(uid)).thenReturn(patient)
    whenever(preferredLanguageRepository.getByCode(LANGUAGE_FR)).thenReturn(preferredLanguage)
    whenever(smsTemplateRepository.getByLanguage(LANGUAGE_FR)).thenReturn(Result.failure(Exception()))
    whenever(smsTemplateRepository.getByLanguage(LANGUAGE_EN)).thenReturn(Result.success(messageTemplate))
    whenever(smsRepository.send(message)).thenReturn(Result.success(fakeOutboundResponse))

    val result = sendSmsUseCase.invoke(uid)

    assert(result is SmsResult.SuccessUsingEn)
  }

  @Test
  fun `WHEN No Template Found For AnyLanguage THEN returns TemplateFailure`() = runTest {
    val patient = Patient(
      uid = uid,
      number = "123456789",
      name = fakeName,
      phone = "123456789",
      preferredLanguage = LANGUAGE_FR
    )

    whenever(patientRepository.getByUid(uid)).thenReturn(patient)
    whenever(smsTemplateRepository.getByLanguage(LANGUAGE_FR)).thenReturn(Result.failure(Exception()))
    whenever(smsTemplateRepository.getByLanguage(LANGUAGE_EN)).thenReturn(Result.failure(Exception()))

    val result = sendSmsUseCase.invoke(uid)

    assert(result is SmsResult.TemplateFailure)
  }

  @Test
  fun `WHEN Sms Sending Fails THEN returns Send Failure`() = runTest {
    val patient = Patient(
      uid = uid,
      number = "123456789",
      name = fakeName,
      phone = "123456789",
      preferredLanguage = LANGUAGE_ES
    )
    val messageTemplate = MessageTemplate("Hola {{fullName}}", LANGUAGE_ES)
    val message = Message("Hola $fakeName", listOf("123456789"))

    whenever(patientRepository.getByUid(uid)).thenReturn(patient)
    whenever(smsTemplateRepository.getByLanguage(LANGUAGE_ES)).thenReturn(Result.success(messageTemplate))
    whenever(smsRepository.send(message)).thenReturn(Result.failure(Exception()))

    val result = sendSmsUseCase.invoke(uid)

    assert(result is SmsResult.SendFailure)
  }


}