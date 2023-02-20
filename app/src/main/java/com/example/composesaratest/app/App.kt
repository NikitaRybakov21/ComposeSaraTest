package com.example.composesaratest.app

import android.app.Application
import com.yandex.mapkit.MapKitFactory

class App : Application() {
    override fun onCreate() {
        super.onCreate()

        MapKitFactory.setApiKey("4292d98f-ba3c-4caf-96f1-a2bc4993a4da")
    }
}