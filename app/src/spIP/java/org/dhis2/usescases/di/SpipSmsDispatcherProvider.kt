package org.dhis2.usescases.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.dhis2.commons.viewmodel.DispatcherProvider

class SpipSmsDispatcherProvider : DispatcherProvider {
  override fun io(): CoroutineDispatcher {
    return Dispatchers.IO
  }

  override fun computation(): CoroutineDispatcher {
    return Dispatchers.Default
  }

  override fun ui(): CoroutineDispatcher {
    return Dispatchers.Main
  }
}