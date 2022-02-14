package com.dputera.cigalator.db

import android.app.Application
import androidx.lifecycle.LiveData
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@DelicateCoroutinesApi
class CigaretteTimeRepository(application: Application) {

    private var cigaretteTimeDAO: CigaretteTimeDAO

    private val database = CigaretteTimeDB.getInstance(application)

    init {
        cigaretteTimeDAO = database.cigaretteTimeDao()
    }

    fun insert(cigaretteTime: CigaretteTime) {
        GlobalScope.launch {
            cigaretteTimeDAO.insert(cigaretteTime)
        }
    }

    fun delete(cigaretteTime: CigaretteTime) {
        GlobalScope.launch {
            cigaretteTimeDAO.delete(cigaretteTime)
        }
    }

    fun deleteAllData() {
        GlobalScope.launch {
            cigaretteTimeDAO.deleteAllData()
        }
    }

    fun getAllData() : List<CigaretteTime> {
        var result : List<CigaretteTime> = ArrayList()
        GlobalScope.launch {
            result = cigaretteTimeDAO.getAllData()
        }
        return result
    }

    fun getTodayCigarette(dayOfYear : Int, year : Int) : List<CigaretteTime>{
        var result : List<CigaretteTime> = ArrayList()
        GlobalScope.launch {
           result = cigaretteTimeDAO.getTodayCig(dayOfYear, year)
        }
        return result
    }

    fun getThisWeekCigarette(weekOfYear : Int, year : Int) : List<CigaretteTime>{
        var result : List<CigaretteTime> = ArrayList()
        GlobalScope.launch {
            result = cigaretteTimeDAO.getThisWeekCig(weekOfYear, year)
        }
        return result
    }

    fun getThisMonthCigarette(weekOfYear : Int, year : Int) : List<CigaretteTime>{
        var result : List<CigaretteTime> = ArrayList()
        GlobalScope.launch {
            result = cigaretteTimeDAO.getThisWeekCig(weekOfYear, year)
        }
        return result
    }

    fun getThisYearCigarette(year : Int) : List<CigaretteTime>{
        var result : List<CigaretteTime> = ArrayList()
        GlobalScope.launch {
            result = cigaretteTimeDAO.getThisYearCig( year)
        }
        return result
    }

}