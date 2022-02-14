package com.dputera.cigalator.db

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import kotlinx.coroutines.DelicateCoroutinesApi

@DelicateCoroutinesApi
class CigaretteTimeViewModel(app: Application) : AndroidViewModel(app) {

    private val repository = CigaretteTimeRepository(app)
    private val allData = repository.getAllData()

    fun getAllData() : List<CigaretteTime>{
        return allData
    }

    fun insert(cigaretteTime: CigaretteTime){
        repository.insert(cigaretteTime)
    }

    fun delete(cigaretteTime: CigaretteTime){
        repository.delete(cigaretteTime)
    }

    fun deleteAllData(){
        repository.deleteAllData()
    }

    fun getTodayCigarette(dayOfYear : Int, year : Int) : List<CigaretteTime> {
        return repository.getTodayCigarette(dayOfYear, year)
    }

    fun getThisWeekCigarette(weekOfYear : Int, year : Int){
        repository.getThisWeekCigarette(weekOfYear, year)
    }

    fun getThisMonthCigarette(monthOfYear : Int, year : Int){
        repository.getThisMonthCigarette(monthOfYear, year)
    }

    fun getThisYearCigarette(year : Int){
        repository.getThisYearCigarette( year)
    }
}