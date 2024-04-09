package com.greyhouse34.gh34mobile;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

public class SplashActivity extends AppCompatActivity {

    public static int Splash_time = 3400;
    //private RelativeLayout damysplashbg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


       // damysplashbg = findViewById(R.id.mysplashbg);
       // TransitionDrawable trans = (TransitionDrawable) damysplashbg.getBackground();
       // trans.startTransition(2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
            }
        },Splash_time);

    }
    /** Called when the user touches the mysplashBTN button */
    public void closeSplash(View view) {
        // Do something in response to button click
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
    }
    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Sad to see you go 😢... \n Are you Sure you want to Exit gH34Mobile App?")
                .setCancelable(false)
                .setPositiveButton("Yep", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        AlertDialog alertDialog=builder.create();
        alertDialog.show();
    }

}
