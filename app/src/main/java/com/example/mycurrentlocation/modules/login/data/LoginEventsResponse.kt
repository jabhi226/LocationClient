package com.example.mycurrentlocation.modules.login.data

import com.example.mycurrentlocation.modules.login.models.Driver

interface LoginEventsResponse {
    fun onLoginResponse(event: Driver?)
    fun onLoginError(event: String)
}