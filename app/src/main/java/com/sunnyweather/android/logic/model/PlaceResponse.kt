package com.sunnyweather.android.logic.model

import com.google.gson.annotations.SerializedName

/**
 * create by Peng
 * on 2020-11-17 22:44
 *
 */

data class PlaceResponse(val status: String, val places : List<Place>)

/**
 * @SerializedName 注解可以使JSON 和 kotlin 之间建立映射关系
 */
data class Place(val name : String, val location: Location, @SerializedName("formatted_address") val address : String)

data class Location(val lng : String, val lat : String)