package com.sunnyweather.android

import android.app.Application
import android.content.Context

/**
 * create by Peng
 * on 2020-11-17 22:41
 *
 */
class SunnyWeatherApplication : Application() {

    companion object {
        const val TOKEN = "kKY5wyqFZBlqUQ76"

        lateinit var context : Context
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}