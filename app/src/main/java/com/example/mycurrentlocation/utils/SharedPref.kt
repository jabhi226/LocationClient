package com.example.mycurrentlocation.utils

import android.app.Activity
import android.content.Context
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object SharedPref {

    private const val TAG = "CURRENT_LOCATION_APP_DATA"

    const val IP_ADDRESS = "IP_ADDRESS"

    fun getString(context: Context?, key: String?): String {
        var returnString = ""
        if (context != null) {
            val sp = context.getSharedPreferences(TAG, Activity.MODE_PRIVATE)
            returnString = sp.getString(key, "").toString()
        }
        return returnString
    }

    fun setString(context: Context?, key: String?, value: String?) {
        if (context != null) {
            val sharedPreferences = context.getSharedPreferences(TAG, Activity.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            editor.apply()
        }
    }
}
