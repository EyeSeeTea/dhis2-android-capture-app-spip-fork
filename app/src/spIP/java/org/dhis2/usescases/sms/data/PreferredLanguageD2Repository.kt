package org.dhis2.usescases.sms.data

import org.dhis2.usescases.sms.domain.patient.PreferredLanguage
import org.dhis2.usescases.sms.domain.patient.PreferredLanguageRepository
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.option.Option

const val optionSet ="Bvg8UG9v84X"

class PreferredLanguageD2Repository (private val d2:D2): PreferredLanguageRepository {
    override fun getByCode(code: String): PreferredLanguage {
        val options = d2.optionModule().options()
            .byOptionSetUid().eq(optionSet)
            .byCode().eq(code)
            .blockingGet()

         val option = options.firstOrNull()
             ?: throw IllegalArgumentException("No Option found with code: $code")

        return buildPreferredLanguage(option, code)
    }

    private fun buildPreferredLanguage(
        option: Option,
        code: String
    ): PreferredLanguage {
        return PreferredLanguage(
            uid = option.uid(),
            code = code,
            name = option.displayName() ?: code
        )
    }
}