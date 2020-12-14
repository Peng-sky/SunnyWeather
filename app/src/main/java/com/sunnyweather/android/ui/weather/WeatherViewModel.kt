package com.sunnyweather.android.ui.weather

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Location

/**
 * create by Peng
 * on 2020-12-08 21:37
 *
 */
class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<Location>()

    var locationLng = ""

    var locationLat = ""

    var placeName  = ""

    //调用仓库层的代码将，将仓库层的代码转换成Activity 可观察的liveData 对象
    //switchMap 观察locationLiveData 对象，并在 switchMap() 方法的转换函数中调用 refreshWeather
    val weatherLiveData = Transformations.switchMap(locationLiveData) {location ->
        Repository.refreshWeather(location.lng, location.lat)
    }

    fun refreshWeather(lng: String, lat :String) {
        locationLiveData.value = Location(lng, lat)
    }
}