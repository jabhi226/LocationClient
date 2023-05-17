package com.example.mycurrentlocation.modules.home

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import com.example.mycurrentlocation.MyBackgroundTask
import com.example.mycurrentlocation.MyLocationFinderClass
import com.example.mycurrentlocation.R
import java.util.Timer
import java.util.TimerTask

class MainActivity : AppCompatActivity(), OnTouchListener {
    private var timer: Timer? = null
    private var stopButton: Button? = null
    private var startButton: Button? = null
    private var textView: TextView? = null
    private var infoText: TextView? = null
    private var driverUsername: String? = null
    private var counter = false
    private var editText: EditText? = null
    private var layoutParams: RelativeLayout.LayoutParams? = null
    private var width = 0
    private var height = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()

        // this will pop-up alert box to allow permission (this will run only at the first launch)
        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            10
        )
        driverUsername = intent.extras?.getString("driver_username_key", "defaultKey")
        textView?.text = "Uesename : $driverUsername"
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
            val dr = resources.getDrawable(R.drawable.login_btn)
            startButton?.background = dr
            infoText?.text = "Location is beeing tracked"
        }
        if (MotionEvent.ACTION_UP == event.action) {
            startButton?.textSize = 20f
            layoutParams?.width = width
            layoutParams?.height = height
            startButton?.layoutParams = layoutParams
            val dr1 = resources.getDrawable(R.drawable.login_btn2)
            startButton?.background = dr1
            stopButton?.visibility = View.VISIBLE
            startButton?.visibility = View.INVISIBLE
            timer = Timer()
            timer?.schedule(RepeaterClass(applicationContext), 0, 5000)
            //Todo (start service here and in service start timer)
        }
    }

    private fun stopLocationService(event: MotionEvent) {
        if (MotionEvent.ACTION_DOWN == event.action) {
            stopButton?.textSize = 23f
            layoutParams?.width = width + 30
            layoutParams?.height = height + 5
            stopButton?.layoutParams = layoutParams
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val dr = resources.getDrawable(R.drawable.login_btn)
                stopButton?.background = dr
            }
            val vibrator1 = getSystemService(VIBRATOR_SERVICE) as Vibrator
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator1.vibrate(
                    VibrationEffect.createOneShot(
                        30,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                //deprecated in API 26
                vibrator1.vibrate(30)
            }
        }
        if (MotionEvent.ACTION_UP == event.action) {
            stopButton?.textSize = 20f
            layoutParams?.width = width
            layoutParams?.height = height
            stopButton?.layoutParams = layoutParams
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                val dr1 = resources.getDrawable(R.drawable.login_btn2)
                stopButton?.background = dr1
            }
            stopButton?.visibility = View.INVISIBLE
            startButton?.visibility = View.VISIBLE
            infoText?.text = "Tap below button to Start"
            if (timer != null) {
                timer?.cancel()
                timer = null
            }
            if (counter) {
                counter = false
                Toast.makeText(applicationContext, "Location Service Has Stop", Toast.LENGTH_SHORT)
                    .show()
                val lat = "0"
                val lon = "0"
                val context1: Context = this@MainActivity
                val myBackgroundTask = MyBackgroundTask(context1)
                myBackgroundTask.execute(lat, lon, driverUsername)
            }
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
                    val lat = "0"
                    val lon = "0"
                    val context1: Context = this@MainActivity
                    val myBackgroundTask = MyBackgroundTask(context1)
                    myBackgroundTask.execute(lat, lon, driverUsername)
                }
                finish()
            }
            .setNegativeButton("No", null)
            .show()
    }

    //this will repeat the service
    internal inner class RepeaterClass(var context: Context) : TimerTask() {
        var editText: EditText? = null
        var context1: Context = this@MainActivity

        //MyLocationFinderClass will access the current location
        var myLocationFinderClass = MyLocationFinderClass(applicationContext)
        var handler = Handler()
        override fun run() {
            handler.post {
                //location will contain the current location attribute
                val location = myLocationFinderClass.location
                if (location != null) {
                    val lat = location.latitude.toString()
                    val lon = location.longitude.toString()
                    val myBackgroundTask = MyBackgroundTask(context1)
                    myBackgroundTask.execute(lat, lon, driverUsername)
                    editText = findViewById<View>(R.id.editText) as EditText
                    editText?.setText("$lat,$lon")
                    editText?.visibility = View.INVISIBLE

                    //Toast.makeText(getApplicationContext(),"New Location Set",Toast.LENGTH_SHORT).show();
                    //Toast.makeText(getApplicationContext(),"Location Successfully Accessed",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(applicationContext, "location null", Toast.LENGTH_SHORT).show()
                    //stopGettingLocation(null);
                }
            }
        }
    }
}