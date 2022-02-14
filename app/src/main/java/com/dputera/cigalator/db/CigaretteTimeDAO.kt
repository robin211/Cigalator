package com.dputera.cigalator.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface CigaretteTimeDAO {
    @Query("SELECT * FROM cigarette_time_table")
    fun getAllData(): List<CigaretteTime>

    @Insert
    fun insert(vararg cigaretteTime: CigaretteTime)

    @Delete
    fun delete(cigaretteTime: CigaretteTime)

    @Query("delete from cigarette_time_table")
    fun deleteAllData()

    @Query("SELECT * FROM cigarette_time_table WHERE day_of_year = :dayOfYear AND year = :year")
    fun getTodayCig(dayOfYear : Int, year : Int) : List<CigaretteTime>

    @Query("SELECT * FROM cigarette_time_table " +
            "WHERE week_of_year = :weekOfYear " +
            "AND year = :year")
    fun getThisWeekCig(weekOfYear : Int, year : Int) : List<CigaretteTime>

    @Query("SELECT * FROM cigarette_time_table " +
            "WHERE month_of_year = :monthOfYear " +
            "AND year = :year")
    fun getThisMonthCig(monthOfYear : Int, year : Int) : List<CigaretteTime>

    @Query("SELECT * FROM cigarette_time_table " +
            "WHERE year = :year")
    fun getThisYearCig(year : Int) : List<CigaretteTime>
}