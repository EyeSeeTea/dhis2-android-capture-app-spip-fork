package org.dhis2.usescases.sms.domain.repository.message

import org.dhis2.usescases.sms.data.model.MessageTemplate
import org.dhis2.usescases.sms.domain.model.types.Maybe

interface MessageTemplateRepository {
  suspend fun getByLanguage(language: String): Maybe<MessageTemplate>
}