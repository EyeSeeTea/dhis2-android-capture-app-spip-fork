package org.dhis2.usescases.sms.data.api

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class D2Constant(
    val id: String,
    val name: String,
    val description: String)

interface ConstantApi {
    @GET("constants/{id}")
    fun getConstant(
        @Path("id") id: String,
        @Query("fields") fields: String = "id,name,description"
    ): Call<D2Constant>
}
