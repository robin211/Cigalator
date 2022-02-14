package com.dputera.cigalator.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_option.*
import kotlinx.android.synthetic.main.activity_option.loading
import kotlinx.android.synthetic.main.fragment_preference.*
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class OptionActivity : AppCompatActivity() {
    lateinit var sp : SharedPreferences
    lateinit var spTheme : SharedPreferences
    private var mInterstitialAd: InterstitialAd? = null
    private var mAdIsLoading: Boolean = false
    private var isStatistic = false
    private var openTIme = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        spTheme = this.getSharedPreferences(SP_NAME_THEME, Context.MODE_PRIVATE)
        when (spTheme.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)) {
            THEME_CALMING_BLUE -> setTheme(R.style.CalmingBlueTheme)
            THEME_DARK_YET_STRIKING -> setTheme(R.style.DarkYetStriking)
            THEME_KNIGHT_OF_THE_DARK -> setTheme(R.style.KnightOfTheDark)
        }
        setContentView(R.layout.activity_option)

        when (intent.getIntExtra(OPTION_TYPE, 1)) {
            OPTION_TYPE_SETTING -> {
                title_option.text = getString(R.string.setting)
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_holeder, SettingFragment.newInstance(), TAG_FRAGMENT_SETTING)
                    .commit()
            }
            OPTION_TYPE_PREFERENCE -> {
                title_option.text = getString(R.string.preference)
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_holeder, PreferenceFragment.newInstance(), TAG_FRAGMENT_PREFERENCE)
                    .commit()
            }
            OPTION_TYPE_STATISTIC -> {
                isStatistic = true
                openTIme = Calendar.getInstance().timeInMillis
                title_option.text = getString(R.string.statistic)
                supportFragmentManager
                    .beginTransaction()
                    .add(R.id.fragment_holeder, StatisticFragment.newInstance(), TAG_FRAGMENT_STATISTIC)
                    .commit()
            }
        }

        btn_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        if (isStatistic){
            val currentTime = Calendar.getInstance().timeInMillis
            val timePassed = currentTime - openTIme
            if (timePassed > 10000){
                loadAd()
            }else {
             super.onBackPressed()
            }
        }else{
            super.onBackPressed()
        }
    }

    private fun loadAd() {
        showLoading()
        val adRequest = AdRequest.Builder().build()
        InterstitialAd
            .load(
                this, AD_UNIT_ID, adRequest,
                object : InterstitialAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        hideLoading()
                        Log.d("Preference", adError.message)
                        mInterstitialAd = null
                        mAdIsLoading = false
                        isStatistic = false
                        onBackPressed()
                    }

                    override fun onAdLoaded(interstitialAd: InterstitialAd) {
                        hideLoading()
                        Log.d("Preference", "Ad was loaded.")
                        mInterstitialAd = interstitialAd
                        mAdIsLoading = false
                        showInterstitial()
                    }

                }
            )

    }

    private fun showInterstitial() {
        if (mInterstitialAd != null) {
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    Log.d("Ad", "Ad was dismissed.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
//                    loadAd()
                    isStatistic = false
                    onBackPressed()
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
                    Log.d("Ad", "Ad failed to show.")
                    // Don't forget to set the ad reference to null so you
                    // don't show the ad a second time.
                    mInterstitialAd = null
                    isStatistic = false
                    onBackPressed()
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d("Ad", "Ad showed fullscreen content.")
                    // Called when ad is dismissed.
                }
            }
            mInterstitialAd?.show(this@OptionActivity)
        } else {
            Log.d("Ad", "Ad wasn't loaded.")
        }
    }

    fun showLoading(){
        loading.visibility = View.VISIBLE
        loading.startAnim(3000)
    }

    fun hideLoading(){
        loading.stopAnim()
        loading.visibility = View.GONE
    }

}