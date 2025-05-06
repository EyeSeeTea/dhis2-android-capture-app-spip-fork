package org.dhis2.usescases.sms.data

import org.dhis2.usescases.sms.data.api.ConstantApi
import org.dhis2.usescases.sms.domain.message.MessageTemplate
import org.dhis2.usescases.sms.domain.message.MessageTemplateRepository
import org.dhis2.usescases.sms.domain.types.Maybe
import org.hisp.dhis.android.core.D2

class MessageTemplateD2Repository (private val d2: D2, private val constantApi: ConstantApi) : MessageTemplateRepository {
    override fun getByLanguage(language: String): Maybe<MessageTemplate> {
        val templateConstants = d2.constantModule().constants().byName()
            .eq("CMO_ENROLLMENT_TEMPLATE_$language")
            .blockingGet()

        if (templateConstants.isEmpty()){
            return Maybe.None
        }

        val templateConstant = templateConstants.first()

        val description = getDescriptionConstant(templateConstant.uid())

        if (description.isEmpty()){
            return Maybe.None
        }

        return Maybe.Some( MessageTemplate(
            text = description,
            language = language
        ))
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