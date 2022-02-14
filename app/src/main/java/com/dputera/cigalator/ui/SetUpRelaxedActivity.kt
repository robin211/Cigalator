package com.dputera.cigalator.ui

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.Window
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.coroutines.DelicateCoroutinesApi
import java.util.*

@DelicateCoroutinesApi
class SetUpRelaxedActivity : AppCompatActivity() {
    lateinit var result: LinearLayout
    lateinit var openingDesc: TextView
    lateinit var txtNever: TextView
    lateinit var emergencyDesc: TextView
    lateinit var button: TransitionButton
    lateinit var etMaxCig: EditText
    lateinit var etMaxEmergency: EditText
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
        setContentView(R.layout.activity_set_up_relaxed)

        result = findViewById(R.id.break_holder)
        openingDesc = findViewById(R.id.opening_desc)
        txtNever = findViewById(R.id.never)
        emergencyDesc = findViewById(R.id.emergency_desc)
        button = findViewById(R.id.btn_ok)
        etMaxCig = findViewById(R.id.et_max_cig)
        etMaxEmergency = findViewById(R.id.et_max_emergencies)

        etMaxCig.addTextChangedListener(object : TextWatcher {

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
                } else {
                    openingDesc.visibility = View.GONE
                    result.visibility = View.GONE
                    txtNever.visibility = View.GONE
                    emergencyDesc.visibility = View.GONE
                    button.text = getString(R.string.please_input_max_cigarettes)
                }
            }
        })
    }

    private fun calculateBreakTime(amount: Int) {
        val txtHour = findViewById<TextView>(R.id.hour)
        val txtMinute = findViewById<TextView>(R.id.minute)
        val txtSecond = findViewById<TextView>(R.id.second)

        if (amount > 0) {
            val breakTimeInMilis = ONE_DAY_IN_MILLIS / amount
            val breakTimeHour = breakTimeInMilis / HOUR_IN_MILLIS
            val remainderBreakTimeHour = breakTimeInMilis % HOUR_IN_MILLIS
            val breakTimeMinute = remainderBreakTimeHour / MINUTE_IN_MILLIS
            val remainderBreakTimeMinute = remainderBreakTimeHour % MINUTE_IN_MILLIS
            val breakTimeSecond = remainderBreakTimeMinute / SECOND_IN_MILLIS

            txtHour.text = breakTimeHour.toString()
            txtMinute.text = breakTimeMinute.toString()
            txtSecond.text = breakTimeSecond.toString()

            txtNever.visibility = View.GONE
            emergencyDesc.visibility = View.VISIBLE
            result.visibility = View.VISIBLE
            button.text = getString(R.string.continues)
            button.setOnClickListener {
                showDialog()
            }
        } else {
            txtNever.visibility = View.VISIBLE
            emergencyDesc.visibility = View.GONE
            result.visibility = View.GONE
            button.text = getString(R.string.please_input_max_cigarettes)
        }
        openingDesc.visibility = View.VISIBLE
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
        val currentMaxCigarette = etMaxCig.text.toString().toInt()
        var currentMaxEmergency = 3
        if (etMaxEmergency.text.isNotEmpty()) {
            currentMaxEmergency = etMaxEmergency.text.toString().toInt()
        }
        val currentTimeForOne = ONE_DAY_IN_MILLIS / currentMaxCigarette
        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        val currentWeeokOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val currentYear = calendar.get(Calendar.YEAR)
        sharedPreferences.edit()
            .putBoolean(SP_IS_PROGRAM_CHOSEN, true)
            .putInt(SP_CURRENT_MAX_CIGARETTE, currentMaxCigarette)
            .putInt(SP_CURRENT_MAX_EMERGENCY, currentMaxEmergency)
            .putLong(SP_CURRENT_TIME_FOR_ONE, currentTimeForOne.toLong())
            .putLong(SP_LAST_ADDITION, currentTime)
            .putLong(SP_PROGRAM_START_TIME, currentTime)
            .putInt(SP_PROGRAM_TYPE, PROGRAM_TYPE_RELAXED)
            .putInt(SP_CURRENT_CIG_AVAILABLE, 1)
            .putInt(SP_CURRENT_EMERGENCY_AVAILABLE, 0)
            .putInt(SP_STARTING_WEEK, currentWeeokOfYear)
            .putInt(SP_STARTING_YEAR, currentYear)
            .putBoolean(SP_FIRST_WEEK_AVERAGE_FIXED, false)
            .apply()
    }
}