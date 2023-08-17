package com.example.food_order

import android.app.Application
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance

val Profile: ProfilePreference by lazy {
    FoodOrderApplication.prefs!!
}
class FoodOrderApplication :Application(){
    companion object {
        var prefs: ProfilePreference? = null
    }

    override fun onCreate() {
        super.onCreate()
        prefs = ProfilePreference(this)
//        prefs!!.baseUrl="http://192.168.56.1//:80"
    }

}