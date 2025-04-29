package org.dhis2.usescases.teiDashboard.ui

import android.view.View
import org.dhis2.usescases.teiDashboard.TeiDashboardMenuCustomActionsManager
import javax.inject.Inject

class TeiDashboardMenuCustomActionsManagerImpl @Inject constructor(

): TeiDashboardMenuCustomActionsManager {
  override fun sendSms(teiUid: String?, parentView: View) {
    TODO("Not yet implemented - Only available in SPIP version")
  }

  override fun onDestroy() {
    TODO("Not yet implemented - Only available in SPIP version")
  }
}