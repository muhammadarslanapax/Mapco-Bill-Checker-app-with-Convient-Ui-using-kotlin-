package com.bill.mepcobill

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.FrameLayout
import com.applovin.mediation.MaxAd
import com.applovin.mediation.MaxAdViewAdListener
import com.applovin.mediation.MaxError
import com.applovin.mediation.ads.MaxAdView
import com.applovin.sdk.AppLovinSdk
import com.applovin.sdk.AppLovinSdkConfiguration

import com.google.android.gms.ads.MobileAds

import kotlinx.android.synthetic.main.activity_confirm.*

class confirm : AppCompatActivity(), MaxAdViewAdListener {
    private var adView: MaxAdView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_confirm)
        AppLovinSdk.getInstance( this ).setMediationProvider( "max" )
        AppLovinSdk.getInstance( this ).initializeSdk({ configuration: AppLovinSdkConfiguration ->
            // AppLovin SDK is initialized, start loading ads
            // call function

            createBannerAd2()


        })
        val number1 = intent.getStringExtra("maindata").toString()
        text3.text =number1.toString()








        confirm.setOnClickListener {
            startActivity(            Intent(this, LoadActivity::class.java).putExtra("t1",number1.toString())
            )


            //https://bill.pitc.com.pk/mepcobill/general?refno=02158220111609
















          //  finishAffinity()

        }




    }
    fun createBannerAd2()
    {
        adView = MaxAdView("3e0eef20975359af", this)
        adView?.setListener(this)

        // Stretch to the width of the screen for banners to be fully functional
        val width = ViewGroup.LayoutParams.MATCH_PARENT

        // Banner height on phones and tablets is 50 and 90, respectively
        val heightPx = resources.getDimensionPixelSize(R.dimen.banner_height)

        adView?.layoutParams = FrameLayout.LayoutParams(width, heightPx)

        // Set background or background color for banners to be fully functional
        //  adView?.setBackgroundColor(...)

        val rootView = findViewById<ViewGroup>(android.R.id.content)
        rootView.addView(adView)

        // Load the ad
        adView?.loadAd()
    }

    override fun onAdLoaded(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdDisplayed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdHidden(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdClicked(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdLoadFailed(adUnitId: String?, error: MaxError?) {
        TODO("Not yet implemented")
    }

    override fun onAdDisplayFailed(ad: MaxAd?, error: MaxError?) {
        TODO("Not yet implemented")
    }

    override fun onAdExpanded(ad: MaxAd?) {
        TODO("Not yet implemented")
    }

    override fun onAdCollapsed(ad: MaxAd?) {
        TODO("Not yet implemented")
    }


}