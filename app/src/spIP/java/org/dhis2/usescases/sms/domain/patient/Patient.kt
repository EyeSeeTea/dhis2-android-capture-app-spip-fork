package org.dhis2.usescases.sms.domain.patient

data class Patient(val uid:String,
                   val number:String,
                   val name:String,
                   val phone:String,
                   val preferredLanguage:String)