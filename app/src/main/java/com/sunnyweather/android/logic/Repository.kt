package com.sunnyweather.android.logic

import androidx.lifecycle.liveData
import com.sunnyweather.android.logic.dao.PlaceDao
import com.sunnyweather.android.logic.model.Place
import com.sunnyweather.android.logic.model.Weather
import com.sunnyweather.android.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.Exception
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext


/**
 * create by Peng
 * on 2020-11-18 21:53
 *
 */
object Repository {

    /**
     * liveData(Dispatchers.IO) {} 此函数可以自动构建并返回一个 liveData对象，并且在代码块中提供了一个挂机函数的上下文
     * 这样就可以在 liveData() 函数的代码块中调用任意的挂机函数
     * Dispatchers.IO 代表在子线程中执行
     *
     * 通过kotlin 内置的 Result.success() 来包装获取的城市数据列表
     * 最后使用 emit()方法（发射） 来将包装的结果发射出去，类似liveData.setValue() 来通知数据变化
     */
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
        val placeResponse = SunnyWeatherNetwork.searchPlaces(query)
        if (placeResponse.status == "ok") {
            val places = placeResponse.places
            Result.success(places)
        } else {
            Result.failure(RuntimeException("response status is ${placeResponse.status}"))
        }
    }


    /**
     * 同时将两个接口获取到的数据返回到一个Weather对象中
     * async 必须在协程作用域下才能使用，会返回一个 Deferred 对象，如果想要获取async 代码块的执行结果 可以调用
     * Deferred 对象的 await() 方法即可
     */
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {
        coroutineScope {
            val deferredRealtime = async {
                SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
            }
            val deferredDaily = async {
                SunnyWeatherNetwork.getDailyWeather(lng, lat)
            }
            val realTimeResponse = deferredRealtime.await()

            val dailyResponse = deferredDaily.await()

            if (realTimeResponse.status == "ok" && dailyResponse.status == "ok") {
                val weather = Weather(realTimeResponse.result.realtime, dailyResponse.result.daily)

                Result.success(weather)
            } else {
                Result.failure(
                    RuntimeException(
                        "realtime response status is ${realTimeResponse.status}" +
                                "daily response status is ${dailyResponse.status}"
                    )
                )
            }
        }
    }

    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()


    /**
     * 这是一个按照liveData()函数的参数接收标准定义的一个高阶函数。在Fire对象的内部会先调用一下 liveData() 函数
     * 然后再 liveData() 函数代码块中统一进行 try catch 处理，并在try 语句中传入 lambda 表达式中的代码
     *
     * 在lambda 表达式中的代码也一定是在挂起函数中运行的，需要在函数类型前声明一个suspend 关键字，
     * 用以表达lambda 表达式中的代码也是拥有挂起函数的上下文的。
     *
     */
    private fun <T> fire (context: CoroutineContext, block: suspend () -> Result<T>) = liveData<Result<T>>(context) {
        val result = try {
            block()
        } catch (e: Exception) {
            Result.failure<T>(e)
        }
        emit(result)
    }
}