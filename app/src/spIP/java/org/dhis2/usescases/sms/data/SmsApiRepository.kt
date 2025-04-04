package org.dhis2.usescases.sms.data

import org.dhis2.usescases.sms.data.api.OutboundApi
import org.dhis2.usescases.sms.data.api.OutboundRequest
import org.dhis2.usescases.sms.domain.message.Message
import org.dhis2.usescases.sms.domain.message.SmsRepository

class SmsApiRepository (private val outboundApi: OutboundApi) : SmsRepository {
    override fun send(message: Message) {
        try {
            val response = outboundApi.sendSms(OutboundRequest(
                message = message.text,
                recipients = message.recipients
            )).execute()

            if (!response.isSuccessful) {
                throw Exception("Error sending message: ${response.errorBody()?.string()}")
            }
        }catch (e: Exception){
            throw Exception("Error sending message: ${e.message}")
        }
    }
}