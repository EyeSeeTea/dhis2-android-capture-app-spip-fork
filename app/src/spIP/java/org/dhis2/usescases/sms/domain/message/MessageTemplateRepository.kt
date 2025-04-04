package org.dhis2.usescases.sms.domain.message

interface MessageTemplateRepository {
    fun getByLanguage(language: String): MessageTemplate
}