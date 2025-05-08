package org.dhis2.usescases.sms.data.repository.sms

import org.dhis2.usescases.sms.data.api.OutboundApi
import org.dhis2.usescases.sms.data.model.OutboundRequest
import org.dhis2.usescases.sms.domain.model.sms.Message
import org.dhis2.usescases.sms.domain.repository.sms.SmsRepository

class SmsApiRepository(
  private val outboundApi: OutboundApi
) : SmsRepository {

  override suspend fun send(message: Message) {
    try {
      outboundApi.sendSms(OutboundRequest(
        message = message.text,
        recipients = message.recipients
      ))
    }catch (e: Exception){
      throw Exception("Error sending message: ${e.message}")
    }
  }

}