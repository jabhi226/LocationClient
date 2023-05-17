package com.example.mycurrentlocation.modules.login.data

import com.example.mycurrentlocation.modules.login.models.LoginSuccessResponse
import com.example.mycurrentlocation.utils.Endpoints
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.Exception

class LoginApiCalls(private val loginEvents: LoginEventsResponse) {

    fun login(un: String, pw: String) {
        try {
            Thread {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("http://${Endpoints.IP}:3001/driver/login?username=$un&password=$pw")
                    .addHeader("Content-Type", "application/json")
                    .build()
                val response = client.newCall(request).execute()

                when (response.code) {
                    200 -> {
                        val res = JSONObject(response.body?.string().toString())
                        println("----> $res")
                        if (res.optString("status").uppercase() == "OK") {
                            val loginSuccessResponse = Gson().fromJson(
                                res.toString(),
                                LoginSuccessResponse::class.java
                            )
                            loginEvents.onLoginResponse(loginSuccessResponse.result)
                        } else {
                            loginEvents.onLoginError(res.optString("result"))
                        }
                    }

                    else -> {
                        loginEvents.onLoginError("Something went wrong.")
                    }
                }
            }.start()
        } catch (e: Exception){
            e.printStackTrace()
            loginEvents.onLoginError("Something went wrong.")
        }
    }
}