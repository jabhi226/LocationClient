package com.example.mycurrentlocation.modules.home.service

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.mycurrentlocation.modules.home.data.MainApiCalls
import com.example.mycurrentlocation.modules.home.data.MainEventsResponse
import com.example.mycurrentlocation.utils.SharedPref
import com.example.mycurrentlocation.utils.dateToStringConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import java.lang.Exception
import java.util.Date

class BackgroundWorker(
    private val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {
    override suspend fun doWork(): Result {
        return try {
            val getLocation = CoroutineScope(Dispatchers.Main).async {
                MyLocationFinderClass(context).location
            }
            val location = getLocation.await()

            SharedPref.setString(context, "LOGS", SharedPref.getString(context, "LOGS") + Date().dateToStringConverter() + "\n")

            MainApiCalls(
                mainEventsResponse,
                SharedPref.getString(context, SharedPref.IP_ADDRESS)
            ).sendLatLong("101", location?.latitude ?: 0.0, location?.longitude ?: 0.0)

            repeatAfterSometime(20 * 1000L)
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            repeatAfterSometime(20 * 1000L)
            Result.failure()
        }
    }

    private suspend fun repeatAfterSometime(time: Long) {
        delay(time)
        val workerRequest =
            OneTimeWorkRequestBuilder<BackgroundWorker>()
                .addTag(BackgroundWorker::class.java.simpleName)
                .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            "BackgroundWorker",
            ExistingWorkPolicy.REPLACE,
            workerRequest
        )
    }


    private val mainEventsResponse = object : MainEventsResponse {
        override fun onSendLatLongSuccess() {
            println("-----> BackgroundWorker Success")
        }

        override fun onSendLatLongError() {
            println("-----> BackgroundWorker Error")
        }
    }

}