package org.dhis2.usescases.sms.domain.message

import org.dhis2.usescases.sms.domain.types.Maybe

interface MessageTemplateRepository {
    fun getByLanguage(language: String): Maybe<MessageTemplate>
}