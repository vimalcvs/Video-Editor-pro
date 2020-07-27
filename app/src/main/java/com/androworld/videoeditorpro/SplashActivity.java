package com.androworld.videoeditorpro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.InterstitialAd;

public class SplashActivity extends AppCompatActivity {


    private Ads ads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ads = new Ads();
        if (ads.mInterstitialAd == null) {
            ads.loadAd(SplashActivity.this);
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                openmainactivity();
            }
        }, 4000);
    }

    private void openmainactivity() {
        ads.loadd(new Ads.AdLisoner() {
            @Override
            public void onSucssec(InterstitialAd mInterstitialAd) {
                startActivity(new Intent(SplashActivity.this, Mainactivity.class));
                finish();
            }

            @Override
            public void onun() {
                startActivity(new Intent(SplashActivity.this, Mainactivity.class));
                finish();
            }
        });

    }
}
