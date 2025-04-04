package org.dhis2.usescases.sms.data.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

data class OutboundResponse(
    val httpStatus: String,
    val httpStatusCode: Int,
    val status: String,
    val message: String
)

data class OutboundRequest(
    val message: String,
    val recipients: List<String>
)

interface OutboundApi {
    @POST("sms/outbound")
    fun sendSms(@Body request: OutboundRequest): Call<OutboundResponse>
}
