package org.dhis2.usescases.sms.data

import org.dhis2.usescases.sms.domain.patient.Patient
import org.dhis2.usescases.sms.domain.patient.PatientRepository
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstance

const val numberAtt ="Pntz2rubsPu"
const val firstNameAtt = "SinKvMFe2mD"
const val lastNameAtt = "nOguXiyCUSv"
const val phoneAtt = "yXS3uuFF5ul"
const val preferredLanguageAtt = "VnOpPm1uZJR"

class PatientD2Repository (private val d2:D2): PatientRepository {
    override fun getByUid(uid: String): Patient {
        val tei = d2.trackedEntityModule().trackedEntityInstances()
            .withTrackedEntityAttributeValues().uid(uid).blockingGet()
            ?: throw IllegalArgumentException("No TEI found with uid: $uid")

        return buildPatient(tei)
    }

    private fun buildPatient(tei: TrackedEntityInstance): Patient {
        val number = getAttributeValue(tei, numberAtt)
        val firstName = getAttributeValue(tei, firstNameAtt)
        val lastName = getAttributeValue(tei, lastNameAtt)
        val phone = getAttributeValue(tei, phoneAtt)
        val preferredLanguage = getAttributeValue(tei, preferredLanguageAtt)

        return Patient(
            uid = tei.uid(),
            number = number,
            name = "$firstName $lastName",
            phone = phone,
            preferredLanguage = preferredLanguage
        )
    }

    private fun getAttributeValue(tei: TrackedEntityInstance, attribute: String): String {
        return tei.trackedEntityAttributeValues()?.find { it.trackedEntityAttribute() == attribute }
            ?.value() ?: ""
    }
}