package com.dputera.cigalator.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.provider.Settings.Secure
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.room.Room
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.dputera.cigalator.db.CigaretteTimeDB
import com.dputera.cigalator.db.DATABASE_NAME
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.DelicateCoroutinesApi


@DelicateCoroutinesApi
class MainActivity : AppCompatActivity() {
    lateinit var db: CigaretteTimeDB
    lateinit var sharedPreferences: SharedPreferences
    lateinit var spTheme: SharedPreferences

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        spTheme = this.getSharedPreferences(SP_NAME_THEME, Context.MODE_PRIVATE)
        when (spTheme.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)) {
            THEME_CALMING_BLUE -> setTheme(R.style.CalmingBlueTheme)
            THEME_DARK_YET_STRIKING -> setTheme(R.style.DarkYetStriking)
            THEME_KNIGHT_OF_THE_DARK -> setTheme(R.style.KnightOfTheDark)
        }
        setContentView(R.layout.activity_main)

        MobileAds.initialize(this) {
            println("ad initialization status : $it")
        }

        val deviceId = Secure.getString(
            this.contentResolver,
            Secure.ANDROID_ID
        )
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(listOf(deviceId))
                .build()
        )

        db = Room.databaseBuilder(this, CigaretteTimeDB::class.java, DATABASE_NAME).build()
        if(sharedPreferences.getBoolean(SP_IS_PROGRAM_CHOSEN, false)){
            goToHomeActivity()
        }

        val btnOk : TransitionButton = findViewById(R.id.btn_ok)

        startOpeningAnimation()

        btnOk.setOnClickListener {
            btnOk.startAnimation()
            val handler = Handler()
            handler.postDelayed({
                val isSuccessful = true
                if (isSuccessful) {
                    btnOk.stopAnimation(
                        TransitionButton.StopAnimationStyle.EXPAND
                    ) {
                        goToChooseProgram()
                    }
                } else {
                    btnOk.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null)
                }
            }, 200)
        }

    }

    private fun startOpeningAnimation() {
        val slowFade: TransitionSet = TransitionSet()
            .addTransition(Fade())
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(2000)

        val slideBottom: TransitionSet = TransitionSet()
            .addTransition(Slide(Gravity.BOTTOM))
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(300)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout_container, slowFade)
            findViewById<ImageView>(R.id.smoke).visibility = View.VISIBLE
        }, 50)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout_container, slideBottom)
            findViewById<TransitionButton>(R.id.btn_ok).visibility = View.VISIBLE
        }, 2050)
    }

    private fun goToChooseProgram() {
        val intent = Intent(this, ChooseProgramActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}