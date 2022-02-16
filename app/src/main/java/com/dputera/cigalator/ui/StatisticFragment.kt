package com.dputera.cigalator.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.dputera.cigalator.ExpandableRecordData
import com.dputera.cigalator.R
import com.dputera.cigalator.RecordData
import com.dputera.cigalator.adapter.ExpendableRecordAdapter
import com.dputera.cigalator.constants.*
import com.dputera.cigalator.db.*
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.fragment_statistic.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


@Suppress("DEPRECATION")
@DelicateCoroutinesApi
class StatisticFragment : Fragment() {
    private lateinit var db: CigaretteTimeDB
    private lateinit var sp: SharedPreferences
    private var isFirstWeek = false
    private var dayOfYear = 0
    private var monthOfYear = 0
    private var weekOfYear = 0
    private var mYear = 0
    private lateinit var allCigData: List<CigaretteTime>

    private val HAPPY = "when happy"
    private val SAD = "when sad"
    private val ANGRY = "when angry"
    private val BORED = "when bored"
    private val TENSED = "when tensed"
    private val CRAVING = "while craving"
    private val AFTER_MEAL = "after meal"
    private val AFTER_SEX = "after sex"
    private val ON_BREAK = "while on break"
    private val TOILET = "while in toilet"
    private val EMPHATIC = "after seeing other people smokes"
    private val WORKING = "while working"




    companion object {
        fun newInstance(): StatisticFragment {
            return StatisticFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_statistic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        db = CigaretteTimeDB.getInstance(requireContext())
        sp = requireContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        val calendar = Calendar.getInstance()
        dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        weekOfYear = calendar.get(Calendar.WEEK_OF_YEAR)
        monthOfYear = calendar.get(Calendar.MONTH)
        mYear = calendar.get(Calendar.YEAR)
        if (weekOfYear == sp.getInt(SP_STARTING_WEEK, 0)) {
            if (mYear == sp.getInt(SP_STARTING_YEAR, 0)) {
                isFirstWeek = true
            }
        }

        getAllData()
    }

    private fun getAllData() {
        GlobalScope.launch {
            val allData = db.cigaretteTimeDao().getAllData()
            if (allData.isNotEmpty()) {
                allCigData = allData
                no_data_analysis.visibility = View.GONE
                no_data_record.visibility = View.GONE
                setToday(allCigData)
                setMonth(allCigData)
                setYear(allCigData)
                setWeek(allCigData)
                setRecordList(allCigData)
            } else {
                this_week.visibility = View.GONE
                this_week_1.visibility = View.GONE
                this_week_2.visibility = View.GONE
                this_week_3.visibility = View.GONE
                this_week_4.visibility = View.GONE
                this_week_5.visibility = View.GONE
                this_week_6.visibility = View.GONE
                no_data_analysis.visibility = View.VISIBLE
                no_data_record.visibility = View.VISIBLE
            }
        }
    }

    private fun setRecordList(allData: List<CigaretteTime>) {
        val listYear = ArrayList<Int>()
        for (x in allData) {
            if (listYear.isEmpty()) listYear.add(x.year)
            else {
                if (!listYear.contains(x.year)) listYear.add(x.year)
            }
        }
        val listYearData = ArrayList<ExpandableRecordData>()
        for (year in listYear) {
            val listMonth = ArrayList<Int>()
            for (x in allData) {
                if (x.year == year) {
                    if (listMonth.size == 0) listMonth.add(x.monthOfYear)
                    else {
                        if (!listMonth.contains(x.monthOfYear)) listMonth.add(x.monthOfYear)
                    }
                }
            }
            val listMonthData = ArrayList<RecordData.YearData.Monthdata>()
            for (month in listMonth) {
                val listTime = ArrayList<RecordData.YearData.Monthdata.TimeData>()
                for (y in allData) {
                    if (y.year == year && y.monthOfYear == month) {
                        var isMood = true
                        if (y.isEventRelated) isMood = false
                        val reason = getReason(isMood, y.reason)
                        listTime.add(
                            RecordData.YearData.Monthdata.TimeData(
                                y.timeOfDay,
                                isMood,
                                reason
                            )
                        )
                    }
                }
                val listTimeData = listTime.toList()
                val monthx = RecordData.YearData.Monthdata(getStringMonth(month), listTimeData)
                listMonthData.add(monthx)
            }
            val yearx = RecordData.YearData(year, listMonthData.toList())
            listYearData.add(
                ExpandableRecordData(
                    ExpandableRecordData.PARENT,
                    yearx,
                    false,
                    false
                )
            )
        }
        requireActivity().runOnUiThread {
            val layoutManager = LinearLayoutManager(requireContext())
            val adapter = ExpendableRecordAdapter(requireContext(), listYearData.toMutableList())
            rv_record.layoutManager = layoutManager
            rv_record.adapter = adapter
        }
    }

    private fun getReason(mood: Boolean, reason: Int): String {
        var reasonx = ""
        if (mood) {
            when (reason) {
                DB_MOOD_HAPPY -> reasonx = "Happy"
                DB_MOOD_SAD -> reasonx = "Sad"
                DB_MOOD_ANGRY -> reasonx = "Angry"
                DB_MOOD_BORED -> reasonx = "Bored"
                DB_MOOD_TENSED -> reasonx = "Tensed"
                DB_MOOD_CRAVING -> reasonx = "Craving"
            }
        } else {
            when (reason) {
                DB_EVENT_AFTER_MEAL -> reasonx = "After Meal"
                DB_EVENT_AFTER_SEX -> reasonx = "After Sex"
                DB_EVENT_ON_BREAK -> reasonx = "On Break"
                DB_EVENT_IN_TOILET -> reasonx = "In the Toilet"
                DB_EVENT_EMPHATIC -> reasonx = "Saw Other People"
                DB_EVENT_WORKING -> reasonx = "Working"
            }
        }
        return reasonx
    }

    @SuppressLint("SetTextI18n")
    private fun setWeek(allData: List<CigaretteTime>) {
        val listWeekData = ArrayList<CigaretteTime>()
        for (x in allData) {
            if (x.weekOfYear == weekOfYear && x.year == mYear) {
                listWeekData.add(x)
            }
        }
        cig_this_week.text = listWeekData.size.toString()
        var dayCount = 0
        var day = 0
        for (x in listWeekData) {
            if (day == 0) {
                dayCount += 1
                day = x.dayOfWeek
            } else {
                if (x.dayOfWeek != day) {
                    dayCount += 1
                    day = x.dayOfWeek
                }
            }
        }
        val weekAverage: Float = listWeekData.size.toFloat() / dayCount.toFloat()
        if (isFirstWeek) {
            sp.edit().putFloat(SP_FIRST_WEEK_AVERAGE, weekAverage).apply()
        } else {
            if (sp.getBoolean(SP_FIRST_WEEK_AVERAGE_FIXED, false)) {
                calculateFirstWeekAverage(allData)
            }
        }
        cig_average.text = "%.2f".format(sp.getFloat(SP_FIRST_WEEK_AVERAGE, 0.0F))
        setWeeklyAnalysis(listWeekData, sp.getFloat(SP_FIRST_WEEK_AVERAGE, 0.0F), weekAverage)
    }

    private fun setWeeklyAnalysis(
        listWeekData: List<CigaretteTime>,
        firstWeekAverage: Float,
        weekAverage: Float
    ) {
        populateFirstAnalysis(firstWeekAverage, weekAverage)
        populateSecondAnalysis(listWeekData)
        populateThirdAnalysis(listWeekData)
        populateFourthAnalysis(listWeekData)
        populateFifthAnalysis(listWeekData)
        populateSixthAnalysis(listWeekData)
    }

    @SuppressLint("SetTextI18n")
    private fun populateSixthAnalysis(listWeekData: List<CigaretteTime>) {
        val reasonList = mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        for (x in listWeekData) {
            if (x.isMoodRelated) {
                val idx = x.reason.minus(1)
                reasonList[idx] += 1
            } else {
                val idx = x.reason.plus(5)
                reasonList[idx] += 1
            }
        }
        val max = reasonList.maxOrNull() ?: 0
        var stringReason = ""
        for (d in 0 until reasonList.size) {
            if (reasonList[d] == max) {
                val sReason = getStringReason(d)
                if (stringReason == "") {
                    stringReason = sReason
                } else {
                    stringReason += ", $sReason"
                }
            }
        }
        requireActivity().runOnUiThread {
            txt_this_week_6.text = "You smoke the most ($max) $stringReason."
        }

    }

    private fun getStringReason(index: Int): String {
        var result = ""
        when (index) {
            0 -> result = HAPPY
            1 -> result = SAD
            2 -> result = ANGRY
            3 -> result = BORED
            4 -> result = TENSED
            5 -> result = CRAVING
            6 -> result = AFTER_MEAL
            7 -> result = AFTER_SEX
            8 -> result = ON_BREAK
            9 -> result = TOILET
            10 -> result = EMPHATIC
            11 -> result = WORKING
        }
        return result
    }

    @SuppressLint("SetTextI18n")
    private fun populateFifthAnalysis(listWeekData: List<CigaretteTime>) {
        var moodCount = 0
        var eventCount = 0
        for (x in listWeekData) {
            if (x.isMoodRelated) moodCount += 1
            else eventCount += 1
        }
        requireActivity().runOnUiThread {
            when {
                moodCount > eventCount -> txt_this_week_5.text =
                    "Your mood influenced most of your decision to smoke ($moodCount)."
                eventCount > moodCount -> txt_this_week_5.text =
                    "Your decision to smoke mostly influenced by an event around or happened to you ($eventCount)."
                moodCount == eventCount -> txt_this_week_5.text =
                    "Your mood and event around you, influenced the same amount of your decision to smoke ($moodCount)."
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun populateFourthAnalysis(listWeekData: List<CigaretteTime>) {
        val dayList = mutableListOf(0, 0, 0, 0, 0, 0, 0)
        for (x in listWeekData) {
            val dayOfWeek = x.dayOfWeek.minus(1)
            dayList[dayOfWeek] += 1
        }
        val max = dayList.maxOrNull() ?: 0
        var stringDays = ""
        for (d in dayList) {
            if (d == max) {
                val sday = getStringDay(dayList.indexOf(d))
                if (stringDays == "") stringDays = sday
                else stringDays += ", $sday"
            }
        }
        txt_this_week_4.text = "You have smoke the most ($max) on $stringDays."
    }

    @SuppressLint("SetTextI18n")
    private fun populateThirdAnalysis(listWeekData: List<CigaretteTime>) {
        val dayArray = arrayOf(6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17)
        val nightArray = arrayOf(18, 19, 20, 21, 22, 23, 0, 1, 2, 3, 4, 5)
        var dayAmount = 0
        var nightAmount = 0
        for (x in listWeekData) {
            if (dayArray.contains(x.hourOfDay)) dayAmount += 1
            if (nightArray.contains(x.hourOfDay)) nightAmount += 1
        }
        requireActivity().runOnUiThread {
            when {
                dayAmount > nightAmount -> {
                    txt_this_week_3.text =
                        "You smoke more during the day ($dayAmount) than at night ($nightAmount)."
                }
                nightAmount > dayAmount -> {
                    txt_this_week_3.text =
                        "You smoke more at night ($nightAmount) than during the day ($dayAmount)."
                }
                else -> {
                    txt_this_week_3.text =
                        "You smoke the same amount during the day and at night ($dayAmount)."
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun populateSecondAnalysis(listWeekData: List<CigaretteTime>) {
        val listHour =
            mutableListOf(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)
        val listHourSmoke: ArrayList<Int> = ArrayList()
        for (x in listWeekData) {
            val h = x.hourOfDay
            listHour[h] += 1
        }
        val max = listHour.maxOrNull() ?: 0
        var stringHour = ""
        for (x in 0 until listHour.size) {
            if (listHour[x] == max) listHourSmoke.add(x)
        }
        for (x in listHourSmoke) {
            var stringX = "$x:00"
            var stringX1 = x.plus(1).toString() + ":00"
            if (x < 10) stringX = "0$stringX"
            if (x.plus(1) < 10) stringX1 = "0$stringX1"
            if (stringHour == "") stringHour = "$stringX - $stringX1"
            else stringHour += ", $stringX - $stringX1"
        }
        txt_this_week_2.text = "The most you have smoke ($max) are between $stringHour."
    }

    @SuppressLint("SetTextI18n")
    private fun populateFirstAnalysis(firstWeekAverage: Float, weekAverage: Float) {
        val favg = "%.2f".format(firstWeekAverage)
        val avg = "%.2f".format(weekAverage)
        if (isFirstWeek) {
            txt_this_week_1.text =
                "You are in your first week, current weekly average are $avg cigarette(s) per day."
        } else {
            when {
                weekAverage < firstWeekAverage -> {
                    txt_this_week_1.text =
                        "On average ($avg), you smoke less than your first week ($favg) of this program. Nicely done!"
                }
                weekAverage > firstWeekAverage -> {
                    txt_this_week_1.text =
                        "On average ($avg), you smoke more than your first week ($favg) of this program. Don't stress, we believe in you!"
                }
                else -> {
                    txt_this_week_1.text =
                        "On average ($avg), you smoke the same amount of your first week ($favg) of this program."
                }
            }
        }
    }

    private fun calculateFirstWeekAverage(allData: List<CigaretteTime>) {
        val firstWeekData = ArrayList<CigaretteTime>()
        for (x in allData) {
            if (x.weekOfYear == sp.getInt(SP_STARTING_WEEK, 0) &&
                x.year == sp.getInt(SP_STARTING_YEAR, 0)
            ) {
                firstWeekData.add(x)
            }
        }
        var dayCount = 0
        var day = 0
        for (x in firstWeekData) {
            if (day == 0) {
                dayCount += 1
                day = x.dayOfWeek
            } else {
                if (x.dayOfWeek != day) {
                    dayCount += 1
                    day = x.dayOfWeek
                }
            }
        }
        val weekAverage: Float = firstWeekData.size.toFloat() / dayCount.toFloat()
        sp.edit().putFloat(SP_FIRST_WEEK_AVERAGE, weekAverage)
            .putBoolean(SP_FIRST_WEEK_AVERAGE_FIXED, true)
            .apply()
    }

    private fun setYear(allData: List<CigaretteTime>) {
        var dataCount = 0
        for (x in allData) {
            if (x.year == mYear) {
                dataCount += 1
            }
        }
        cig_this_year.text = dataCount.toString()
    }

    private fun setMonth(allData: List<CigaretteTime>) {
        var dataCount = 0
        for (x in allData) {
            if (x.monthOfYear == monthOfYear && x.year == mYear) {
                dataCount += 1
            }
        }
        cig_this_month.text = dataCount.toString()
    }

    private fun setToday(allData: List<CigaretteTime>) {
        var dataCount = 0
        for (x in allData) {
            if (x.dayOfYear == dayOfYear && x.year == mYear) {
                dataCount += 1
            }
        }
        cig_today.text = dataCount.toString()
    }

    private fun getStringDay(index: Int): String {
        var result = ""
        when (index) {
            0 -> result = "Sunday"
            1 -> result = "Monday"
            2 -> result = "Tuesday"
            3 -> result = "Wednesday"
            4 -> result = "Thursday"
            5 -> result = "Friday"
            6 -> result = "Saturday"
        }

        return result
    }

    private fun getStringMonth(index: Int): String {
        var result = ""
        when (index) {
            0 -> result = "January"
            1 -> result = "February"
            2 -> result = "March"
            3 -> result = "April"
            4 -> result = "May"
            5 -> result = "June"
            6 -> result = "July"
            7 -> result = "August"
            8 -> result = "September"
            9 -> result = "October"
            10 -> result = "November"
            11 -> result = "December"
        }

        return result
    }
}