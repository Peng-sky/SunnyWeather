package com.sunnyweather.android.logic.model

/**
 * create by Peng
 * on 2020-12-03 22:37
 *
 */
data class Weather(val realtime: RealTimeResponse.RealTime, val daily: DailyResponse.Daily)