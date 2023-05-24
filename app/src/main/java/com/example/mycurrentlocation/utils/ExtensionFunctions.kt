package com.example.mycurrentlocation.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


fun Date.dateToStringConverter(format: String = "yyyy-MM-dd HH:mm:ss"): String {
    val formattedDate =
        SimpleDateFormat(format, Locale.ENGLISH)
    return formattedDate.format(this)
}