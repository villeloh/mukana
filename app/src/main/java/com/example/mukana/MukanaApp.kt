package com.example.mukana

import android.app.Application
import com.facebook.soloader.SoLoader

class MukanaApp : Application() {

    override fun onCreate() {
        super.onCreate()

        // loads Litho dependencies
        SoLoader.init(this, false)
    }
}