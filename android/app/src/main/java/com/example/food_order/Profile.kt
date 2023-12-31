package com.example.food_order

import android.content.Context
import android.content.SharedPreferences

class ProfilePreference (var context: Context? = null){
    private val prefsFilename = "com.example.food_order_new.prefs"
    private var prefs: SharedPreferences? = null
    val baseUrl="http://192.168.56.1//:80"
    init {
        this.prefs = context?.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)
    }
    var sid: String
        set(value) {
            this.prefs?.edit()?.putString("sid", value)?.apply()
        }
        get() = prefs?.getString("sid", "") ?: ""


}