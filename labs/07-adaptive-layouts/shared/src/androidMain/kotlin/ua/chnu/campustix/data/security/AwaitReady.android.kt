package ua.chnu.campustix.data.security

import eu.anifantakis.lib.ksafe.KSafe

actual suspend fun KSafe.awaitReady() = Unit
