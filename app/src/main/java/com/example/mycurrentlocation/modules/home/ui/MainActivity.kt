package com.example.mycurrentlocation.modules.home.ui

import android.Manifest
import android.app.AlertDialog
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.mycurrentlocation.R
import com.example.mycurrentlocation.modules.home.service.BackgroundWorker
import com.example.mycurrentlocation.modules.login.models.Driver
import com.example.mycurrentlocation.utils.SharedPref
import com.example.mycurrentlocation.utils.dateToStringConverter
import java.util.Date
import java.util.Timer

class MainActivity : AppCompatActivity(), OnTouchListener {
    private var timer: Timer? = null
    private var stopButton: Button? = null
    private var startButton: Button? = null
    private var textView: TextView? = null
    private var infoText: TextView? = null
    private var driverUsername: String? = null
    private var driver: Driver? = null
    private var counter = false
    private var editText: EditText? = null
    private var layoutParams: RelativeLayout.LayoutParams? = null
    private var width = 0
    private var height = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        SharedPref.setString(this, "LOGS", "")

        // this will pop-up alert box to allow permission (this will run only at the first launch)
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            10
        )
        val bundle = intent.getBundleExtra("bundle")
        driver = bundle?.getParcelable("driver")
        textView?.text = "Uesename : ${driver?.username}"
        infoText?.text = "Tap below button to Start"
    }

    private fun initView() {
        editText = findViewById<View>(R.id.editText) as EditText
        editText?.visibility = View.INVISIBLE
        textView = findViewById<View>(R.id.textView4) as TextView
        infoText = findViewById<View>(R.id.info_textview) as TextView
        startButton = findViewById<View>(R.id.button) as Button
        stopButton = findViewById<View>(R.id.button2) as Button
        stopButton?.visibility = View.INVISIBLE
        startButton?.setOnTouchListener(this)
        stopButton?.setOnTouchListener(this)
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        layoutParams = v.layoutParams as RelativeLayout.LayoutParams
        height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 65f, resources.displayMetrics)
                .toInt()
        width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 300f, resources.displayMetrics)
                .toInt()
        if (v.id == startButton?.id) {
            startLocationService(event)
        } else if (v.id == stopButton?.id) {
            stopLocationService(event)
        }
        return true
    }

    private fun startLocationService(event: MotionEvent) {
        if (MotionEvent.ACTION_DOWN == event.action) {
            counter = true
            val vibrator1 = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator1.vibrate(
                VibrationEffect.createOneShot(
                    30,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
            startButton?.textSize = 23f
            layoutParams?.width = width + 30
            layoutParams?.height = height + 5
            startButton?.layoutParams = layoutParams
            val dr = ContextCompat.getDrawable(this, R.drawable.login_btn)
            startButton?.background = dr
            infoText?.text = "Location is beeing tracked"
        }
        if (MotionEvent.ACTION_UP == event.action) {
            startButton?.textSize = 20f
            layoutParams?.width = width
            layoutParams?.height = height
            startButton?.layoutParams = layoutParams
            val dr1 = ContextCompat.getDrawable(this, R.drawable.login_btn2)
            startButton?.background = dr1
            stopButton?.visibility = View.VISIBLE
            startButton?.visibility = View.INVISIBLE

            val workerRequest =
                OneTimeWorkRequestBuilder<BackgroundWorker>()
                    .addTag(BackgroundWorker::class.java.simpleName)
                    .build()
            WorkManager.getInstance(this).enqueueUniqueWork(
                "BackgroundWorker",
                ExistingWorkPolicy.KEEP,
                workerRequest
            )
            return
        }
    }

    private fun stopLocationService(event: MotionEvent) {
        if (MotionEvent.ACTION_DOWN == event.action) {
            stopButton?.textSize = 23f
            layoutParams?.width = width + 30
            layoutParams?.height = height + 5
            stopButton?.layoutParams = layoutParams
            val dr = ContextCompat.getDrawable(this, R.drawable.login_btn)
            stopButton?.background = dr
            val vibrator1 = getSystemService(VIBRATOR_SERVICE) as Vibrator
            vibrator1.vibrate(
                VibrationEffect.createOneShot(
                    30,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        }
        if (MotionEvent.ACTION_UP == event.action) {
            stopButton?.textSize = 20f
            layoutParams?.width = width
            layoutParams?.height = height
            stopButton?.layoutParams = layoutParams
            val dr1 = ContextCompat.getDrawable(this, R.drawable.login_btn2)
            stopButton?.background = dr1
            stopButton?.visibility = View.INVISIBLE
            startButton?.visibility = View.VISIBLE
            infoText?.text = "Tap below button to Start"
            WorkManager.getInstance(this)
                .cancelAllWorkByTag(BackgroundWorker::class.java.simpleName)
            return
        }
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Location Tracking")
            .setMessage("Are you sure you want to Sign out?")
            .setPositiveButton("Yes") { dialog, which ->
                if (timer != null) {
                    timer?.cancel()
                    timer = null
                }
                if (counter) {
                    counter = false
                    Toast.makeText(
                        applicationContext,
                        "Location Service Has Been Stoped",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

}