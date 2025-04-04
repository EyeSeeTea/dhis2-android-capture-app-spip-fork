package org.dhis2.usescases.sms.domain.message

data class Message(val text:String, val recipients: List<String>)
