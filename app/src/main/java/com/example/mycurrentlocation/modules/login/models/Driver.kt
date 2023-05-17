package com.example.mycurrentlocation.modules.login.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable


data class Driver(

    @SerializedName("id") var id: Int? = null,
    @SerializedName("name") var name: String? = null,
    @SerializedName("username") var username: String? = null,
    @SerializedName("password") var password: String? = null,
    @SerializedName("busNumber") var busNumber: String? = null,
    @SerializedName("_id") var Id: String? = null,
    @SerializedName("__v") var _v: Int? = null

) : Serializable