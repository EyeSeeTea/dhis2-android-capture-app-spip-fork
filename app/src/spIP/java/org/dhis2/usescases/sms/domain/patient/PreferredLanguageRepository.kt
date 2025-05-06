package org.dhis2.usescases.sms.domain.patient

interface PreferredLanguageRepository {
    fun getByCode(code: String): PreferredLanguage
}