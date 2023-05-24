package com.example.mycurrentlocation.modules.login.models

import android.os.Parcel
import android.os.Parcelable
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

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readValue(Int::class.java.classLoader) as? Int,
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readValue(Int::class.java.classLoader) as? Int
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(name)
        parcel.writeString(username)
        parcel.writeString(password)
        parcel.writeString(busNumber)
        parcel.writeString(Id)
        parcel.writeValue(_v)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Driver> {
        override fun createFromParcel(parcel: Parcel): Driver {
            return Driver(parcel)
        }

        override fun newArray(size: Int): Array<Driver?> {
            return arrayOfNulls(size)
        }
    }
}