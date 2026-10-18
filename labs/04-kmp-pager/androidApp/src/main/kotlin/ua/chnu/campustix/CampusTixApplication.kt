package ua.chnu.campustix

import android.app.Application
import ua.chnu.campustix.di.initApp

class CampusTixApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initApp()
    }
}
