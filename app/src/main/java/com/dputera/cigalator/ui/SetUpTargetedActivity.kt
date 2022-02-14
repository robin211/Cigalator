package com.dputera.cigalator.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.android.synthetic.main.activity_set_up_relaxed.*
import kotlinx.android.synthetic.main.activity_set_up_targeted.*
import kotlinx.android.synthetic.main.activity_set_up_targeted.break_holder
import kotlinx.android.synthetic.main.activity_set_up_targeted.btn_ok
import kotlinx.android.synthetic.main.activity_set_up_targeted.emergency_desc
import kotlinx.android.synthetic.main.activity_set_up_targeted.et_max_emergencies
import kotlinx.android.synthetic.main.activity_set_up_targeted.hour
import kotlinx.android.synthetic.main.activity_set_up_targeted.minute
import kotlinx.android.synthetic.main.activity_set_up_targeted.never
import kotlinx.android.synthetic.main.activity_set_up_targeted.opening_desc
import kotlinx.android.synthetic.main.activity_set_up_targeted.second
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class SetUpTargetedActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spTheme: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        spTheme = this.getSharedPreferences(SP_NAME_THEME, Context.MODE_PRIVATE)
        when (spTheme.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)) {
            THEME_CALMING_BLUE -> setTheme(R.style.CalmingBlueTheme)
            THEME_DARK_YET_STRIKING -> setTheme(R.style.DarkYetStriking)
            THEME_KNIGHT_OF_THE_DARK -> setTheme(R.style.KnightOfTheDark)
        }
        setContentView(R.layout.activity_set_up_targeted)

        et_current_amount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    calculateBreakTime(s.toString().toInt())
                    calculateRecommendation(s.toString().toInt())
                    if(et_target.text.isNotEmpty()){
                        btn_ok.text = getString(R.string.continues)
                        btn_ok.setOnClickListener {
                            showDialog()
                        }
                    }else {
                        btn_ok.text = getString(R.string.please_input_target)
                    }
                } else {
                    opening_desc.visibility = View.GONE
                    break_holder.visibility = View.GONE
                    never.visibility = View.GONE
                    emergency_desc.visibility = View.GONE
                    btn_ok.text = getString(R.string.please_input_current_amount)
                    txt_recommendation.text = getString(R.string.we_will_calculate_a_recommendation_time_after_you_input_current_amount)
                    et_target.hint = getString(R.string._0)
                }
            }
        })

        et_target.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {
            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
                if (s.isNotEmpty()) {
                    if (et_current_amount.text.isNotEmpty()){
                        if (s.toString().toInt() > 0){
                            btn_ok.text = getString(R.string.continues)
                            btn_ok.setOnClickListener {
                                showDialog()
                            }
                        }else{
                            btn_ok.text = getString(R.string.please_input_target)
                        }
                    }else{
                        btn_ok.text = getString(R.string.please_input_current_amount)
                    }
                } else {
                    if (et_current_amount.text.isNotEmpty()){
                        btn_ok.text = getString(R.string.please_input_target)
                    }else{
                        btn_ok.text = getString(R.string.please_input_current_amount)
                    }
                }
            }
        })
    }

    private fun calculateRecommendation(amount: Int) {
        val recommendedTarget = amount * 3
        if (amount > 0){
            txt_recommendation.text = "We would recommend a $recommendedTarget day(s) target for your current situation."
            if (et_target.text.isEmpty()){
                et_target.hint = recommendedTarget.toString()
            }
        }
    }

    private fun calculateBreakTime(amount: Int) {
        if (amount > 0){
            val breakTimeInMilis = ONE_DAY_IN_MILLIS / amount
            val breakTimeHour = breakTimeInMilis / HOUR_IN_MILLIS
            val remainderBreakTimeHour = breakTimeInMilis % HOUR_IN_MILLIS
            val breakTimeMinute = remainderBreakTimeHour / MINUTE_IN_MILLIS
            val remainderBreakTimeMinute = remainderBreakTimeHour % MINUTE_IN_MILLIS
            val breakTimeSecond = remainderBreakTimeMinute / SECOND_IN_MILLIS

            hour.text = breakTimeHour.toString()
            minute.text = breakTimeMinute.toString()
            second.text = breakTimeSecond.toString()

            never.visibility = View.GONE
            emergency_desc.visibility = View.VISIBLE
            break_holder.visibility = View.VISIBLE

        }else {
            never.visibility = View.VISIBLE
            emergency_desc.visibility = View.GONE
            break_holder.visibility = View.GONE
            btn_ok.text = getString(R.string.please_input_max_cigarettes)
            txt_recommendation.text = getString(R.string.we_will_calculate_a_recommendation_time_after_you_input_current_amount)
            et_target.hint = getString(R.string._0)
        }
        opening_desc.visibility = View.VISIBLE
    }

    private fun showDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_one_button_layout)
        val yesBtn = dialog.findViewById(R.id.btn_ok) as TransitionButton
        yesBtn.setOnClickListener {
            saveSharedPreferenceInformation()
            dialog.dismiss()
            goToHomeActivity()
        }
        dialog.show()
    }

    private fun goToHomeActivity() {
        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun saveSharedPreferenceInformation() {
        val currentTarget = et_target.text.toString().toInt()
        val currentAmount = et_current_amount.text.toString().toInt()
        val targetInMilis = currentTarget * ONE_DAY_IN_MILLIS
        val timeToReduce = targetInMilis / currentAmount.toLong()
        val currentTimeForOne = ONE_DAY_IN_MILLIS / currentAmount

        var currentMaxEmergency = 3
        if (et_max_emergencies.text.isNotEmpty()) {
            currentMaxEmergency = et_max_emergencies.text.toString().toInt()
        }
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        val currentWeekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)

        sharedPreferences.edit()
            .putBoolean(SP_IS_PROGRAM_CHOSEN, true)
            .putInt(SP_CURRENT_MAX_CIGARETTE, currentAmount)
            .putInt(SP_CURRENT_MAX_EMERGENCY, currentMaxEmergency)
            .putLong(SP_CURRENT_TIME_FOR_ONE, currentTimeForOne.toLong())
            .putLong(SP_LAST_ADDITION, currentTime)
            .putLong(SP_PROGRAM_START_TIME, currentTime)
            .putInt(SP_PROGRAM_TYPE, PROGRAM_TYPE_TARGETED)
            .putInt(SP_CURRENT_CIG_AVAILABLE, 1)
            .putInt(SP_CURRENT_EMERGENCY_AVAILABLE, 0)
            .putLong(SP_TIME_TO_REDUCE, timeToReduce)
            .putLong(SP_LAST_REDUCTION_TIME, currentTime)
            .putInt(SP_STARTING_WEEK, currentWeekOfYear)
            .putInt(SP_STARTING_YEAR, currentYear)
            .putBoolean(SP_FIRST_WEEK_AVERAGE_FIXED, false)
            .apply()
    }
}