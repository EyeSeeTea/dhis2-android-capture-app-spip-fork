package org.dhis2.usescases.sms.data.api

import org.dhis2.usescases.sms.data.model.D2Constant
import org.hisp.dhis.android.core.arch.api.HttpServiceClient
import javax.inject.Inject

interface ConstantApi {
  suspend fun getConstant(
    id: String,
    fields: String = "id,name,description"
  ): D2Constant
}

class ConstantApiImpl(
  private val client: HttpServiceClient
) : ConstantApi{

  override suspend fun getConstant(
    id: String,
    fields: String
  ): D2Constant {
    return client.get {
      url("constants/$id")
      parameters {
        attribute("fields", fields)
      }
    }
  }
}