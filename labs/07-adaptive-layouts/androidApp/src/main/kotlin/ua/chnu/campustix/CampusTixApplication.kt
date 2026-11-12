package ua.chnu.campustix

import android.app.Application
import org.koin.android.ext.koin.androidContext
import ua.chnu.campustix.di.initApp

class CampusTixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initApp { androidContext(this@CampusTixApplication) }
    }
}
