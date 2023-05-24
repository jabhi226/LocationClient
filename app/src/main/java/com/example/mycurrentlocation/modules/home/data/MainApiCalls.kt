package com.example.mycurrentlocation.modules.home.data

import com.example.mycurrentlocation.utils.SharedPref
import com.example.mycurrentlocation.utils.dateToStringConverter
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainApiCalls(
    private val mainEventsResponse: MainEventsResponse,
    private val ipAddress: String
) {

    fun sendLatLong(busNumber: String, latitude: Double, longitude: Double) {
        try {


            val client = OkHttpClient()
            val mediaType = "application/json".toMediaType()
            val body =
                "{\n    \"busNumber\": \"$busNumber\",\n    \"latitude\": \"$latitude\",\n    \"longitude\": \"$longitude\",\n    \"timestamp\": \"${Date().dateToStringConverter()}\"\n}".toRequestBody(
                    mediaType
                )
            val request = Request.Builder()
                .url("http://${ipAddress}:3001/driverLocation")
                .post(body)
                .addHeader("Content-Type", "application/json")
                .build()
            val response = client.newCall(request).execute()

            when (response.code) {
                200 -> {
                    val res = JSONObject(response.body?.string().toString())
                    println("----> $res")
                    if (res.optString("status").uppercase() == "OK") {
                        mainEventsResponse.onSendLatLongSuccess()
                    } else {
                        mainEventsResponse.onSendLatLongError()
                    }
                }

                else -> {
                    mainEventsResponse.onSendLatLongError()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}