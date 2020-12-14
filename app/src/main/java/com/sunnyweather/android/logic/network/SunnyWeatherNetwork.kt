package com.sunnyweather.android.logic.network

import com.sunnyweather.android.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.RuntimeException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * create by Peng
 * on 2020-11-17 22:57
 *
 * 当外部调用SunnyWeatherNetwork的 searchPlaces() 函数时，Retrofit 就会立即发起网络请求，同时当前协程也会被阻塞住
 * 知道服务器响应我们的请求之后，await() 函数会将解析出来的数据模型对象取出并返回，同时恢复当前协程的执行，
 * searchPlaces() 函数在得到await() 函数的返回值后会将数据再返回到上一层
 */
object SunnyWeatherNetwork {

    //创建出对应接口（PlaceService）的动态代理对象
    private val placeService = ServiceCreator.create<PlaceService>()

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getDailyWeather(lng: String, lat: String) = weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) = weatherService.getRealTimeWeather(lng, lat).await()

    /**
     * 挂起函数 suspend fun.
     * await():  一旦执行此方法就会 通过await() 阻塞当前协程，直到服务器响应我们的请求之后，
     * await() 函数就会将解析出来的数据返回给searchPlaces()，同时恢复当前协程的执行，
     */

    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query).await()


    /**
     * suspendCoroutine 函数
     * 必须在协程作用域，或挂机函数中才能调用， 接收一个lambda 表达式
     * 主要作用： 将当前协程立即挂起，然后在一个普通的线程中执行lambda，lambda 表达式会传入一个 continuation（继续） 参数
     * 调用 continuation 的 resume() / resumeWithException() 可以让协程恢复执行
     *
     * await() 函数
     * await() 函数是一个挂起函数， 声明一个泛型T，并将await()函数定义成 retrofit2.Call<T> 的扩展函数
     * 这样所有返回值是 Call 类型的Retrofit网络请求都可以直接调用await() 函数
     * await() 函数中调用了 suspendCoroutine 函数来挂起当前协程，并且由于扩展函数的原因，我们直接拥有了Call 对象的上下文
     *
     */
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T> {
                override fun onResponse(call: Call<T>, response: Response<T>) {
                    val body = response.body()
                    if (body != null) continuation.resume(body)// -> 该值会成为函数的返回值<T>
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }

                override fun onFailure(call: Call<T>, t: Throwable) {
                    continuation.resumeWithException(t)
                }
            })
        }

    }
}