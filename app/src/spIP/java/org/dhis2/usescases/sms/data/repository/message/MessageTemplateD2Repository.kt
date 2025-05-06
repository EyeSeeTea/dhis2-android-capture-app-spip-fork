package org.dhis2.usescases.sms.data.repository.message

import org.dhis2.usescases.sms.data.api.ConstantApi
import org.dhis2.usescases.sms.data.model.MessageTemplate
import org.dhis2.usescases.sms.domain.model.types.Maybe
import org.dhis2.usescases.sms.domain.repository.message.MessageTemplateRepository
import org.hisp.dhis.android.core.D2

class MessageTemplateD2Repository(
  private val constantApi: ConstantApi,
  private val d2: D2,
) : MessageTemplateRepository {

  override suspend fun getByLanguage(
    language: String
  ): Maybe<MessageTemplate> {
    val templateConstants = d2.constantModule().constants().byName()
      .eq("CMO_ENROLLMENT_TEMPLATE_$language")
      .blockingGet()

    if (templateConstants.isEmpty()) {
      return Maybe.None
    }

    val templateConstant = templateConstants.first()

    val description = getDescriptionConstant(templateConstant.uid())

    if (description.isEmpty()) {
      return Maybe.None
    }

    return Maybe.Some(
      MessageTemplate(
        text = description,
        language = language
      )
    )
  }

  /**
   * Retrieves the description of a constant by its UID.
   *
   * @param uid The UID of the constant to retrieve.
   * @return The description of the constant, or an empty string if not found.
   */
  private suspend fun getDescriptionConstant(uid: String): String {
    return try {
      val body = constantApi.getConstant(uid)
      body.description
    } catch (e: Exception) {
      ""
    }
  }

}