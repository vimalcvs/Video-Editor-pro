package com.androworld.videoeditorpro;

import android.content.Context;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

public class Ads {

    public Ads() {
        //add
    }

    InterstitialAd mInterstitialAd;

    public void loadAd(Context context){
        mInterstitialAd = new InterstitialAd(context);
        mInterstitialAd.setAdUnitId(context.getResources().getString(R.string.InterstitialAd));
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    public void loadd(final AdLisoner adLisoner){
        if (mInterstitialAd.isLoaded()) {
            mInterstitialAd.setAdListener(new AdListener() {
                @Override
                public void onAdLoaded() {
                    //add

                }

                @Override
                public void onAdFailedToLoad(int errorCode) {

                    adLisoner.onun();
                }

                @Override
                public void onAdOpened() {
                    //add

                }

                @Override
                public void onAdClicked() {
                    //add

                }

                @Override
                public void onAdLeftApplication() {
                    //add

                }

                @Override public void onAdClosed() {

                    mInterstitialAd.loadAd(new AdRequest.Builder().build());
                    adLisoner.onSucssec(mInterstitialAd);
                }
            });
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
            } else {
                adLisoner.onun();
            }
        } else {
            adLisoner.onun();
        }
    }

    public interface AdLisoner {
        void onSucssec(InterstitialAd mInterstitialAd);
        void onun();
    }

}