package ua.chnu.campustix.data.security

import eu.anifantakis.lib.ksafe.KSafe
import eu.anifantakis.lib.ksafe.awaitCacheReady

actual suspend fun KSafe.awaitReady() {
    awaitCacheReady()
}
