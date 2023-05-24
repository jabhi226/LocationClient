package com.example.mycurrentlocation.modules.login.ui

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.mycurrentlocation.R
import com.example.mycurrentlocation.modules.home.ui.MainActivity
import com.example.mycurrentlocation.modules.login.data.LoginApiCalls
import com.example.mycurrentlocation.modules.login.data.LoginEventsResponse
import com.example.mycurrentlocation.modules.login.models.Driver
import com.example.mycurrentlocation.utils.SharedPref

class LoginActivity : AppCompatActivity(), OnTouchListener, LoginEventsResponse {

    private var userName: EditText? = null
    private var passWord: EditText? = null
    private var ipAddress: EditText? = null
    private var loginButton: Button? = null
    private var img: ImageView? = null
    private var alertDialog: AlertDialog? = null
    private var layoutParams: RelativeLayout.LayoutParams? = null
    private var height = 0
    private var width = 0
    private val apiCall = LoginApiCalls(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initView()
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            10
        )
    }

    private fun initView() {
        userName = findViewById(R.id.driver_UN)
        ipAddress = findViewById(R.id.ip_address)
        passWord = findViewById<View>(R.id.driver_PW) as EditText
        loginButton = findViewById<View>(R.id.loginButton) as Button
        img = findViewById<View>(R.id.imageView) as ImageView
        loginButton?.setOnTouchListener(this)
        height =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50f, resources.displayMetrics)
                .toInt()
        width =
            TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250f, resources.displayMetrics)
                .toInt()

        findViewById<View>(R.id.textView2)?.setOnLongClickListener {
            ipAddress?.visibility = View.VISIBLE
            true
        }
        ipAddress?.setText(SharedPref.getString(this, SharedPref.IP_ADDRESS))
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        if (v.id == loginButton?.id) {
            layoutParams = v.layoutParams as RelativeLayout.LayoutParams
            if (MotionEvent.ACTION_DOWN == event.action) {
                onBtnDown()
            }
            if (MotionEvent.ACTION_UP == event.action) {
                onBtnUp()
            }
        }
        return true
    }

    private fun onBtnUp() {
        loginButton?.textSize = 20f
        layoutParams?.width = width
        layoutParams?.height = height
        loginButton?.layoutParams = layoutParams
        val dr1 = ContextCompat.getDrawable(this, R.drawable.login_btn2)
        loginButton?.background = dr1
        val un = userName?.text.toString()
        val pw = passWord?.text.toString()
        showLoader()
        SharedPref.setString(this, SharedPref.IP_ADDRESS, ipAddress?.text.toString().trim())
        apiCall.login(un, pw, ipAddress?.text.toString().trim())
    }

    private fun showLoader() {
        val builder = AlertDialog.Builder(this)
        builder.setView(layoutInflater.inflate(R.layout.logging_in_alert_box, null, false))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog?.window?.setBackgroundDrawable(ColorDrawable(0))
        alertDialog?.show()
    }

    private fun onBtnDown() {
        loginButton?.textSize = 23f
        layoutParams?.width = width + 30
        layoutParams?.height = height + 5
        loginButton?.layoutParams = layoutParams
        val dr = ContextCompat.getDrawable(this, R.drawable.login_btn)
        loginButton?.background = dr
        val vibrator1 = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator1.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
    }

    override fun onLoginResponse(driver: Driver?) {
        runOnUiThread {
            alertDialog?.dismiss()
            val intent = Intent(this, MainActivity::class.java)
            val bundle = Bundle()
            bundle.putString("driver_username_key", driver?.username)
            bundle.putParcelable("driver", driver)
            intent.putExtra("bundle", bundle)
            startActivity(intent)
        }
    }

    override fun onLoginError(event: String) {
        runOnUiThread {
            alertDialog?.dismiss()
            Toast.makeText(this, event, Toast.LENGTH_SHORT).show()
        }
    }
}