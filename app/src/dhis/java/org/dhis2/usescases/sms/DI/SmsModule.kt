package org.dhis2.usescases.sms.DI

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.dhis2.usescases.teiDashboard.TeiDashboardMenuCustomActionsManager
import org.dhis2.usescases.teiDashboard.ui.TeiDashboardMenuCustomActionsManagerImpl

@Module
@InstallIn(SingletonComponent::class)
abstract class SmsModule {
  @Binds
  abstract fun bindTeiDashboardMenuCustomActions(impl: TeiDashboardMenuCustomActionsManagerImpl): TeiDashboardMenuCustomActionsManager
}