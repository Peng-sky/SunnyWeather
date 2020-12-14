package com.sunnyweather.android.logic.dao

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.sunnyweather.android.SunnyWeatherApplication
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork

/**
 * create by Peng
 * on 2020-12-14 21:45
 *
 */
object PlaceDao {

    fun savePlace(place : Place) {
        sharedPreference().edit {
            putString("place", Gson().toJson(place))
        }
    }

    fun getSavedPlace() : Place {
        val placeGson = sharedPreference().getString("place", "")
        return Gson().fromJson(placeGson, Place::class.java)
    }

    fun isPlaceSaved() = sharedPreference().contains("place")


    private fun sharedPreference() = SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}