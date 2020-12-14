package com.sunnyweather.android.logic.network

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * create by Peng
 * on 2020-11-17 22:51
 * 单例类
 *
 */
object ServiceCreator {

    private const val BASE_URL = "https://api.caiyunapp.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(getOkHttpClient())
        .build()


    
    fun <T> create(serviceClass: Class<T>) :T = retrofit.create(serviceClass)

    //不带参数的 create() 方法 使用 inline（内联） 来修饰方法， 使用 reified（具体化） 修饰泛型
    //泛型实例化（reified）必须使用 inline 修饰符
    inline fun <reified T> create() : T = create(T::class.java)

    private fun getOkHttpClient(): OkHttpClient? {
        //日志显示级别
        val level = HttpLoggingInterceptor.Level.BODY
        //新建log拦截器
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d(
                "ServiceCreator",
                "OkHttp====Message:$message"
            )
        }
        loggingInterceptor.level = level
        //定制OkHttp
        val httpClientBuilder = OkHttpClient.Builder()
        //OkHttp进行添加拦截器loggingInterceptor
        httpClientBuilder.addInterceptor(loggingInterceptor)
        return httpClientBuilder.build()
    }
}