package org.dhis2.usescases.sms.domain.patient

interface PatientRepository {
    fun getByUid(uid: String): Patient
}