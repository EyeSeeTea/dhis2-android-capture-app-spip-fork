package org.dhis2.usescases.sms.domain.message

interface SmsRepository {
    fun send(message: Message)
}