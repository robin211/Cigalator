@file:Suppress("DEPRECATION")

package com.dputera.cigalator.ui

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.transition.Fade
import android.transition.Slide
import android.transition.TransitionManager
import android.transition.TransitionSet
import android.view.Gravity
import android.view.View
import android.view.Window
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import androidx.lifecycle.ViewModelProviders
import com.dputera.cigalator.R
import com.dputera.cigalator.constants.*
import com.dputera.cigalator.db.*
import com.royrodriguez.transitionbutton.TransitionButton
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

@DelicateCoroutinesApi
class HomeActivity : AppCompatActivity() {
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var spTheme: SharedPreferences
    lateinit var countdown_timer: CountDownTimer
    private lateinit var viewModel: CigaretteTimeViewModel
    private lateinit var db: CigaretteTimeDB
    private var programType : Int = PROGRAM_TYPE_RELAXED
    var timeInSeconds: Long = 0L
    var sideBarOpened = false
    private var themex : Int = 1
    private var isMoodRelated : Boolean = true
    private var reason : Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = this.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        spTheme = this.getSharedPreferences(SP_NAME_THEME, Context.MODE_PRIVATE)
        themex = spTheme.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)
        when (themex) {
            THEME_CALMING_BLUE -> setTheme(R.style.CalmingBlueTheme)
            THEME_DARK_YET_STRIKING -> setTheme(R.style.DarkYetStriking)
            THEME_KNIGHT_OF_THE_DARK -> setTheme(R.style.KnightOfTheDark)
        }
        setContentView(R.layout.activity_home)
        viewModel = ViewModelProviders.of(this)[CigaretteTimeViewModel::class.java]
        db = CigaretteTimeDB.getInstance(this)

        programType = sharedPreferences.getInt(SP_PROGRAM_TYPE, PROGRAM_TYPE_RELAXED)

        if (programType == PROGRAM_TYPE_RELAXED){
            val lastAdditional = sharedPreferences.getLong(SP_LAST_ADDITION, 0L)
            val currentTfo = sharedPreferences.getLong(SP_CURRENT_TIME_FOR_ONE, 0L)
            val nextAdditional = lastAdditional.plus(currentTfo)
            val timeToNextAddition = nextAdditional.minus(Calendar.getInstance().timeInMillis)
            setUpCountDown(timeToNextAddition)
            setUpRelaxedView()
        }else {
            val currentTfo = setUpTargetedView()
            val lastAddition = sharedPreferences.getLong(SP_LAST_ADDITION, 0L)
            val nextAdditions = lastAddition + currentTfo
            val timeToNext = nextAdditions.minus(Calendar.getInstance().timeInMillis)
            setUpCountDownTargeted(timeToNext)
        }

        btn_light.setOnClickListener {
//            startSmokingAnimation()
//            smokeOneCig()
            openDialogWhy()
        }

        btn_menu.setOnClickListener {
            if (sideBarOpened){
                sideBarOpened = false
                sideBar.visibility = View.GONE
                v4.visibility = View.VISIBLE
            }else{
                sideBarOpened = true
                sideBar.visibility = View.VISIBLE
                v4.visibility = View.GONE
            }
        }

        setting.setOnClickListener {
            goToOptionActivity(OPTION_TYPE_SETTING)
        }

        preferences.setOnClickListener {
            goToOptionActivity(OPTION_TYPE_PREFERENCE)
        }

        statistic.setOnClickListener {
            goToOptionActivity(OPTION_TYPE_STATISTIC)
        }
    }

    private fun openDialogWhy() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dialog_why_smoke)
        val happy = dialog.findViewById(R.id.happy) as TextView
        val sad = dialog.findViewById(R.id.sad) as TextView
        val angry = dialog.findViewById(R.id.angry) as TextView
        val bored = dialog.findViewById(R.id.bored) as TextView
        val tensed = dialog.findViewById(R.id.tensed) as TextView
        val craving = dialog.findViewById(R.id.craving) as TextView
        val afterMeal = dialog.findViewById(R.id.after_meal) as TextView
        val afterSex = dialog.findViewById(R.id.after_sex) as TextView
        val onBreak = dialog.findViewById(R.id.on_break) as TextView
        val toilet = dialog.findViewById(R.id.toilet) as TextView
        val emphatic = dialog.findViewById(R.id.emphatic) as TextView
        val working = dialog.findViewById(R.id.working) as TextView
        happy.setOnClickListener {
            isMoodRelated = true
            reason = DB_MOOD_HAPPY
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        sad.setOnClickListener {
            isMoodRelated = true
            reason = DB_MOOD_SAD
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        angry.setOnClickListener {
            isMoodRelated = true
            reason = DB_MOOD_ANGRY
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        bored.setOnClickListener {
            isMoodRelated = true
            reason = DB_MOOD_BORED
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        tensed.setOnClickListener {
            isMoodRelated = true
            reason = DB_MOOD_TENSED
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        craving.setOnClickListener {
            isMoodRelated = true
            reason = DB_MOOD_CRAVING
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        afterMeal.setOnClickListener {
            isMoodRelated = false
            reason = DB_EVENT_AFTER_MEAL
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        afterSex.setOnClickListener {
            isMoodRelated = false
            reason = DB_EVENT_AFTER_SEX
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        onBreak.setOnClickListener {
            isMoodRelated = false
            reason = DB_EVENT_ON_BREAK
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        toilet.setOnClickListener {
            isMoodRelated = false
            reason = DB_EVENT_IN_TOILET
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        emphatic.setOnClickListener {
            isMoodRelated = false
            reason = DB_EVENT_EMPHATIC
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        working.setOnClickListener {
            isMoodRelated = false
            reason = DB_EVENT_WORKING
            startSmokingAnimation()
            smokeOneCig()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun setUpCountDownTargeted(currentTfo: Long) {
        countdown_timer = object : CountDownTimer(currentTfo, 1000) {
            override fun onFinish() {
                val tfo = calculateAdditionalTargeted()
                initView()
                setUpCountDownTargeted(tfo)
            }
            override fun onTick(p0: Long) {
                timeInSeconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()
    }

    private fun setUpCountDown(currentTimeForOne: Long) {
        countdown_timer = object : CountDownTimer(currentTimeForOne, 1000) {
            override fun onFinish() {
                calculateAdditional()
                initView()
                setUpCountDown(currentTimeForOne)
            }
            override fun onTick(p0: Long) {
                timeInSeconds = p0
                updateTextUI()
            }
        }
        countdown_timer.start()
    }

    @SuppressLint("SimpleDateFormat")
    private fun smokeOneCig() {
        val currentCigAvailable = sharedPreferences.getInt(SP_CURRENT_CIG_AVAILABLE, 0)
        val currentEmergencyAvailable = sharedPreferences.getInt(SP_CURRENT_EMERGENCY_AVAILABLE, 0)

        val calendar = Calendar.getInstance()
        val currentTime = calendar.timeInMillis
        val formatter =  SimpleDateFormat("dd MMM yyyy HH:mm")
        val currentTimeofDay = formatter.format(currentTime)
        val hourOfDay = calendar.get(Calendar.HOUR_OF_DAY)
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        val weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        val monthOfYear = calendar.get(Calendar.MONTH)
        val year = calendar.get(Calendar.YEAR)
        val currentMaxCigarette = sharedPreferences.getInt(SP_CURRENT_MAX_CIGARETTE, 0)
        val mood : Boolean
        val event : Boolean
        if (isMoodRelated){
            mood = true
            event = false
        }else{
            mood = false
            event = true
        }
        val cigaretteTime = CigaretteTime(
            currentTimeofDay, hourOfDay, dayOfYear, dayOfWeek, weekOfYear,
            monthOfYear, year, mood, event, reason, currentMaxCigarette
        )
        if (currentCigAvailable > 0){
            sharedPreferences.edit().putInt(SP_CURRENT_CIG_AVAILABLE, 0).apply()
        }
        else {
            if (currentEmergencyAvailable > 0){
                sharedPreferences.edit().putInt(
                    SP_CURRENT_EMERGENCY_AVAILABLE, currentEmergencyAvailable.minus(
                        1
                    )
                ).apply()
            }else {
                sharedPreferences.edit().putInt(
                    SP_CURRENT_CIG_AVAILABLE, currentCigAvailable.minus(
                        1
                    )
                ).apply()
            }
        }
        GlobalScope.launch {
            db.cigaretteTimeDao().insert(cigaretteTime)
        }

        initView()
    }

    private fun startSmokingAnimation() {
        val slowFade: TransitionSet = TransitionSet()
            .addTransition(Fade())
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)

        val slideTop: TransitionSet = TransitionSet()
            .addTransition(Slide(Gravity.TOP))
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(500)

        val slideEnd: TransitionSet = TransitionSet()
            .addTransition(Slide(Gravity.END))
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(200)

        val slideStart: TransitionSet = TransitionSet()
            .addTransition(Slide(Gravity.START))
            .setInterpolator(FastOutSlowInInterpolator())
            .setDuration(200)

        btn_light.visibility = View.GONE

        TransitionManager.beginDelayedTransition(layout, slideTop)
        cigone.visibility = View.INVISIBLE

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slideEnd)
            cig_horizontal.visibility = View.VISIBLE
        }, 500)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slideStart)
            match.visibility = View.VISIBLE
        }, 700)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout)
            burned.visibility = View.VISIBLE
        }, 1200)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slowFade)
            cig_horizontal.visibility = View.GONE
            burned.visibility = View.GONE
            match.visibility = View.GONE
        }, 1950)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slowFade)
            smooth.visibility = View.VISIBLE
        }, 2450)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slowFade)
            smooth.visibility = View.GONE
        }, 3450)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slideTop)
            cigone.visibility = View.VISIBLE
        }, 3950)

        Handler().postDelayed({
            TransitionManager.beginDelayedTransition(layout, slowFade)
            btn_light.visibility = View.VISIBLE
        }, 4250)
    }

    private fun setUpRelaxedView() {
        relaxed_icon.visibility = View.VISIBLE
        targeted_icon.visibility = View.INVISIBLE
        calculateAdditional()
        initView()
    }

    private fun setUpTargetedView() : Long {
        relaxed_icon.visibility = View.INVISIBLE
        targeted_icon.visibility = View.VISIBLE
        return calculateAdditionalTargeted()
    }

    private fun calculateAdditionalTargeted() : Long{
        val lastAdditional = sharedPreferences.getLong(SP_LAST_ADDITION, 0L)
        val currentTimeForOne = sharedPreferences.getLong(SP_CURRENT_TIME_FOR_ONE, 0L)
        val currentTime = Calendar.getInstance().timeInMillis
        var currentCigAvailable = sharedPreferences.getInt(SP_CURRENT_CIG_AVAILABLE, 0)
        var currentEmergencyAvailable = sharedPreferences.getInt(SP_CURRENT_EMERGENCY_AVAILABLE, 0)
        val currentMaxEmergency = sharedPreferences.getInt(SP_CURRENT_MAX_EMERGENCY, 0)
        val currentMaxCigarette = sharedPreferences.getInt(SP_CURRENT_MAX_CIGARETTE, 0)
        val timeToreduce = sharedPreferences.getLong(SP_TIME_TO_REDUCE, 0L)
        val lastReductionTime = sharedPreferences.getLong(SP_LAST_REDUCTION_TIME, 0L)
        val timePassed = currentTime - lastAdditional
        val reductionTimePassed = currentTime - lastReductionTime
        val reductionAmount: Long
        var additionalCig = 0
        var tfo = currentTimeForOne
        if (reductionTimePassed > timeToreduce){
            reductionAmount = reductionTimePassed/timeToreduce
            val listReductionTime = ArrayList<Long>()
            for (i in 1..reductionAmount){
                listReductionTime.add(lastReductionTime.plus(i * timeToreduce))
                if (i == reductionAmount) sharedPreferences.edit()
                    .putLong(SP_LAST_REDUCTION_TIME, lastReductionTime.plus(i * timeToreduce))
                    .apply()
            }
            val listMaxCigarette = ArrayList<Int>()
            for (i in 1..reductionAmount){
                listMaxCigarette.add(currentMaxCigarette - i.toInt())
                if (i == reductionAmount){
                    sharedPreferences.edit()
                        .putInt(SP_CURRENT_MAX_CIGARETTE, currentMaxCigarette - i.toInt()).apply()
                }
            }
            val listAdditionTime = ArrayList<Long>()
            for (x in listMaxCigarette){
                listAdditionTime.add(ONE_DAY_IN_MILLIS / x.toLong())
            }
            var startTime = lastAdditional
            for (x in 0 until listReductionTime.size){
                val passed = listReductionTime[x] - startTime
                val add = passed/listAdditionTime[x]
                additionalCig = additionalCig.plus(add).toInt()
                startTime = startTime.plus(listAdditionTime[x] * add)
            }
            var lastAddTime = startTime
            val nowPassed = currentTime - startTime
            val curMax = sharedPreferences.getInt(SP_CURRENT_MAX_CIGARETTE, 0)
            val curTfO = ONE_DAY_IN_MILLIS / curMax
            if (nowPassed > curTfO){
                additionalCig += (nowPassed / curTfO).toInt()
                lastAddTime += curTfO
            }
            sharedPreferences.edit().putLong(SP_LAST_ADDITION, lastAddTime)
                .putLong(SP_CURRENT_TIME_FOR_ONE, curTfO.toLong()).apply()
            tfo = curTfO.toLong()
        }else{
            additionalCig = (timePassed / currentTimeForOne).toInt()
            sharedPreferences.edit()
                .putLong(
                    SP_LAST_ADDITION,
                    lastAdditional.plus(currentTimeForOne * additionalCig)
                ).apply()
        }

        if (additionalCig > 0){
            if (currentCigAvailable > 0){
                if (currentEmergencyAvailable < currentMaxEmergency){
                    currentEmergencyAvailable += additionalCig
                    if (currentEmergencyAvailable > currentMaxEmergency) currentEmergencyAvailable = currentMaxEmergency
                    sharedPreferences.edit().putInt(
                        SP_CURRENT_EMERGENCY_AVAILABLE,
                        currentEmergencyAvailable
                    ).apply()
                }
            }else{
                currentCigAvailable += additionalCig
                if (currentCigAvailable > 1) {
                    val additionForEmergency = currentCigAvailable - 1
                    currentCigAvailable = 1
                    if (currentEmergencyAvailable < currentMaxEmergency){
                        currentEmergencyAvailable += additionForEmergency
                        if (currentEmergencyAvailable > currentMaxEmergency) currentEmergencyAvailable = currentMaxEmergency
                        sharedPreferences.edit().putInt(
                            SP_CURRENT_EMERGENCY_AVAILABLE,
                            currentEmergencyAvailable
                        ).apply()
                    }
                }
                sharedPreferences.edit().putInt(SP_CURRENT_CIG_AVAILABLE, currentCigAvailable).apply()
            }
        }
        return tfo
    }

    private fun calculateAdditional() {
        val lastAdditional = sharedPreferences.getLong(SP_LAST_ADDITION, 0L)
        val currentTimeForOne = sharedPreferences.getLong(SP_CURRENT_TIME_FOR_ONE, 0L)
        val currentTime = Calendar.getInstance().timeInMillis
        var currentCigAvailable = sharedPreferences.getInt(SP_CURRENT_CIG_AVAILABLE, 0)
        var currentEmergencyAvailable = sharedPreferences.getInt(SP_CURRENT_EMERGENCY_AVAILABLE, 0)
        val currentMaxEmergency = sharedPreferences.getInt(SP_CURRENT_MAX_EMERGENCY, 0)
        val timePassed = currentTime - lastAdditional
        val addition = timePassed / currentTimeForOne
        val additionTime = lastAdditional.plus(addition * currentTimeForOne)
        if (addition.toInt() > 0){
            if (currentCigAvailable > 0){
                if (currentEmergencyAvailable < currentMaxEmergency){
                    currentEmergencyAvailable += addition.toInt()
                    if (currentEmergencyAvailable > currentMaxEmergency) currentEmergencyAvailable = currentMaxEmergency
                    sharedPreferences.edit().putInt(
                        SP_CURRENT_EMERGENCY_AVAILABLE,
                        currentEmergencyAvailable
                    )
                        .putLong(SP_LAST_ADDITION, additionTime).apply()
                }
            }else{
                currentCigAvailable += addition.toInt()
                if (currentCigAvailable > 1) {
                    val additionForEmergency = currentCigAvailable - 1
                    currentCigAvailable = 1
                    if (currentEmergencyAvailable < currentMaxEmergency){
                        currentEmergencyAvailable += additionForEmergency
                        if (currentEmergencyAvailable > currentMaxEmergency) currentEmergencyAvailable = currentMaxEmergency
                        sharedPreferences.edit().putInt(
                            SP_CURRENT_EMERGENCY_AVAILABLE,
                            currentEmergencyAvailable
                        ).apply()
                    }
                }
                sharedPreferences.edit().putInt(SP_CURRENT_CIG_AVAILABLE, currentCigAvailable)
                    .putLong(SP_LAST_ADDITION, additionTime).apply()
            }
        }
    }

    private fun initView() {
        val currentMaxEmergency = sharedPreferences.getInt(SP_CURRENT_MAX_EMERGENCY, 0)
//        val currentTimeForOne = sharedPreferences.getLong(SP_CURRENT_TIME_FOR_ONE, 0L)
        val programStartTime = sharedPreferences.getLong(SP_PROGRAM_START_TIME, 0L)
        val currentCigAvailable = sharedPreferences.getInt(SP_CURRENT_CIG_AVAILABLE, 0)
        val currentEmergencyAvailable = sharedPreferences.getInt(SP_CURRENT_EMERGENCY_AVAILABLE, 0)

        calculateDayPassed(programStartTime)
        determineEmergencyAndBreak(
            currentCigAvailable,
            currentEmergencyAvailable,
            currentMaxEmergency
        )

    }

    @SuppressLint("SetTextI18n")
    private fun updateTextUI() {
        val breakTimeHour = (timeInSeconds / 1000) / 60 / 60
        val breakTimeMinute = (timeInSeconds / 1000 / 60) % 60
        val breakTimeSecond = (timeInSeconds / 1000) % 60
        var hour = breakTimeHour.toString()
        var minute = breakTimeMinute.toString()
        var second = breakTimeSecond.toString()

        if (breakTimeHour < 10) hour = "0$breakTimeHour"
        if (breakTimeMinute < 10) minute = "0$breakTimeMinute"
        if (breakTimeSecond < 10) second = "0$breakTimeSecond"

        countdown.text = "$hour:$minute:$second"
    }

    @SuppressLint("SetTextI18n")
    private fun determineEmergencyAndBreak(
        currentCigAvailable: Int,
        currentEmergencyAvailable: Int,
        currentMaxEmergency: Int
    ) {
        emergency_count.text = "$currentEmergencyAvailable / $currentMaxEmergency"
        txt_cig_avail.text = currentCigAvailable.toString()
    }

    private fun calculateDayPassed(programStartTime: Long) {
        val currentTime = Calendar.getInstance().timeInMillis
        val timeElapsed = currentTime - programStartTime
        val dayPassed = timeElapsed / ONE_DAY_IN_MILLIS

        days_elapsed.text = dayPassed.toString()
        if (dayPassed == 1L) text_day_passed.text = getString(R.string.day_passed)
    }

    override fun onResume() {
        super.onResume()
        val curTheme = spTheme.getInt(SP_CURRENT_THEME, THEME_CALMING_BLUE)
        if (curTheme != themex){
            recreate()
        }else{
            sideBarOpened = false
            sideBar.visibility = View.GONE
            v4.visibility = View.VISIBLE
            if (programType == PROGRAM_TYPE_RELAXED)calculateAdditional()
            else calculateAdditionalTargeted()
            initView()
        }
    }

    private fun goToOptionActivity(optionType: Int) {
        val intent = Intent(this, OptionActivity::class.java)
        intent.putExtra(OPTION_TYPE, optionType)
        startActivity(intent)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }

}