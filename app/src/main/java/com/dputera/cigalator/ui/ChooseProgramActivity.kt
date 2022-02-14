package com.dputera.cigalator.ui

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class ChooseProgramActivity : AppCompatActivity() {
    lateinit var sp : SharedPreferences
    lateinit var spTheme : SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        spTheme = this.getSharedPreferences(SP_NAME_THEME, Context.MODE_PRIVATE)
        when (spTheme.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)) {
            THEME_CALMING_BLUE -> setTheme(R.style.CalmingBlueTheme)
            THEME_DARK_YET_STRIKING -> setTheme(R.style.DarkYetStriking)
            THEME_KNIGHT_OF_THE_DARK -> setTheme(R.style.KnightOfTheDark)
        }
        setContentView(R.layout.activity_choose_program)

        findViewById<RelativeLayout>(R.id.btn_relaxed).setOnClickListener {
            goToSetupRelaxedActivity()
        }

        findViewById<RelativeLayout>(R.id.btn_targeted).setOnClickListener {
            goToSetupTargetedActivity()
        }
    }

    private fun goToSetupTargetedActivity() {
        val intent = Intent(this, SetUpTargetedActivity::class.java)
        startActivity(intent)
    }

    private fun goToSetupRelaxedActivity() {
        val intent = Intent(this, SetUpRelaxedActivity::class.java)
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        if(sp.getBoolean(SP_IS_PROGRAM_CHOSEN, false)){
            goToHomeActivity()
        }
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}