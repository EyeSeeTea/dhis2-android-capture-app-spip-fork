package org.dhis2.usescases.sms.domain.repository.sms

import org.dhis2.usescases.sms.data.model.OutboundResponse
import org.dhis2.usescases.sms.domain.model.sms.Message

interface SmsRepository {
  suspend fun send(message: Message) : Result<OutboundResponse>
}