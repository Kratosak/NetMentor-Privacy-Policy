package com.cisco.quizapp

import android.app.Application
import com.cisco.quizapp.data.local.DatabaseSeeder
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class CiscoQuizApp : Application() {

    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        MobileAds.initialize(this)
        applicationScope.launch {
            databaseSeeder.seedIfEmpty()
        }
    }
}
