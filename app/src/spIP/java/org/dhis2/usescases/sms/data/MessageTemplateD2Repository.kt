package org.dhis2.usescases.sms.data

import org.dhis2.usescases.sms.data.api.ConstantApi
import org.dhis2.usescases.sms.domain.message.MessageTemplate
import org.dhis2.usescases.sms.domain.message.MessageTemplateRepository
import org.hisp.dhis.android.core.D2

class MessageTemplateD2Repository (private val d2: D2, private val constantApi: ConstantApi) : MessageTemplateRepository {
    override fun getByLanguage(language: String): MessageTemplate {
        val templateConstants = d2.constantModule().constants().byName()
            .eq("CMO_ENROLLMENT_TEMPLATE_$language")
            .blockingGet()

        if (templateConstants.isEmpty()){
            throw IllegalArgumentException("No template found for language: $language")
        }

        val templateConstant = templateConstants.first()

        val description = getDescriptionConstant(templateConstant.uid())

        if (description.isEmpty()){
            throw IllegalArgumentException("No description found for template: ${templateConstant.uid()}")
        }

        return MessageTemplate(
            text = description
        )
    }

    private fun getDescriptionConstant(uid:String):String{
        val response = constantApi.getConstant(uid).execute()

        val body = response.body()

        return if (response.isSuccessful && body != null) {
            body.description
        } else{
            ""
        }
    }


}