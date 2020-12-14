package com.sunnyweather.android.ui.place

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.sunnyweather.android.logic.Repository
import com.sunnyweather.android.logic.model.Place

/**
 * create by Peng
 * on 2020-11-19 21:41
 *
 */
class PlaceViewModel : ViewModel() {

    //LiveData 观察 searchPlaces 方法传入的值的数据变化。
    private val searchLiveData = MutableLiveData<String>()

    val placesList = ArrayList<Place>()


    /**
     * 如果ViewModel 中某个LiveData 对象，是调用另外的方法获取的，那么我们就可以借助 switchMap() 方法，
     * 将这个LiveData 对象转换成另外一个可观察的LiveData 对象。
     * 返回一个可供 Activity 观察的LiveData 对象
     */
    val placeLiveData = Transformations.switchMap(searchLiveData) {query ->
        Repository.searchPlaces(query)
    }

    /**
     * 外部调用 searchPlaces 此方法时，不会发起任何请求或者函数调用，只会将传入的query 值设置到 searchLiveData 中，
     * 一旦 searchLiveData 的数据发生变化，那么观察 userIdLiveData 的 switchMap() 方法就会执行，
     * 并且调用我们编写的转换函数，在转换函数中调用 Repository.searchPlaces(query) 来获取真正的用户数据。同时，
     * switchMap 方法会将 Repository.searchPlaces(query) 方法返回的 LiveData 对象转换成一个可观察的 LiveData 对象。
     * 对于Activity 而言，只需要观察这个 placeLiveData 对象就可以了
     *
     */
    fun searchPlaces(query: String) {
        searchLiveData.value = query
    }

    fun savePlace(place: Place) = Repository.savePlace(place)

    fun getSavedPlace() = Repository.getSavedPlace()

    fun isPlaceSaved() = Repository.isPlaceSaved()
}