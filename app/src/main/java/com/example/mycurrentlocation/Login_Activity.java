package com.example.mycurrentlocation;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/*

Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
// Vibrate for 500 milliseconds
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
    v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
} else {
    //deprecated in API 26
    v.vibrate(500);
}

 */
public class Login_Activity extends AppCompatActivity {
    EditText userName,passWord;
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
        userName = findViewById(R.id.driver_UN);
        passWord = (EditText) findViewById(R.id.driver_PW);
        loginButton = (Button) findViewById(R.id.loginButton);

        final Drawable dr = getResources().getDrawable(R.drawable.login_btn);
        final Drawable dr1 = getResources().getDrawable(R.drawable.login_btn2);

        loginButton.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                ConstraintLayout.LayoutParams layoutParams = (ConstraintLayout.LayoutParams) v.getLayoutParams();
                int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
                int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 250, getResources().getDisplayMetrics());

                if(MotionEvent.ACTION_DOWN == event.getAction())
                {
                    loginButton.setTextSize(23);
                    layoutParams.width = width + 30;
                    layoutParams.height = height + 5;
                    loginButton.setLayoutParams(layoutParams);
                    loginButton.setBackground(dr);

                    //vibtare on click
                    Vibrator vibrator1 = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator1.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        vibrator1.vibrate(30);
                    }
                }
                if(MotionEvent.ACTION_UP == event.getAction())
                {
                    loginButton.setTextSize(20);
                    layoutParams.width = width;
                    layoutParams.height = height;
                    loginButton.setLayoutParams(layoutParams);
                    loginButton.setBackground(dr1);

                    String UN = userName.getText().toString();
                    String PW = passWord.getText().toString();

                    Context context = Login_Activity.this;
                    Activity activity = Login_Activity.this;

                    LoginBackgroundTask loginBackgroundTask = new LoginBackgroundTask(context,activity);
                    loginBackgroundTask.execute(UN,PW);

                    /*
                    //startActivity(new Intent(Login_Activity.this, MainActivity.class));
                    Context context = Login_Activity.this;
                    Activity activity = Login_Activity.this;

                    Toast.makeText(getApplicationContext(),"UP",Toast.LENGTH_SHORT).show();

                    LoginBackgroundTask loginBackgroundTask = new LoginBackgroundTask(context,activity);
                    loginBackgroundTask.execute(UN,PW);
                     */
                }

                return true;
            }
        });
    }

    /*public void User_Login(View view) {
        String UN = userName.getText().toString();
        String PW = passWord.getText().toString();

        //startActivity(new Intent(Login_Activity.this, MainActivity.class));

        LoginBackgroundTask loginBackgroundTask = new LoginBackgroundTask(this,this);
        loginBackgroundTask.execute(UN,PW);
    }

     */
}
