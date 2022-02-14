package com.dputera.cigalator.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = CIG_TIME_TABLE_NAME)
data class CigaretteTime(
    @ColumnInfo(name = COLUMN_TIME_OF_DAY) val timeOfDay : String,
    @ColumnInfo(name = COLUMN_HOUR_OF_DAY) val hourOfDay : Int,
    @ColumnInfo(name = COLUMN_DAY_OF_YEAR) val dayOfYear : Int,
    @ColumnInfo(name = COLUMN_DAY_OF_WEEK) val dayOfWeek : Int,
    @ColumnInfo(name = COLUMN_WEEK_OF_YEAR) val weekOfYear : Int,
    @ColumnInfo(name = COLUMN_MONTH_OF_YEAR) val monthOfYear : Int,
    @ColumnInfo(name = COLUMN_YEAR) val year : Int,
    @ColumnInfo(name = COLUMN_MOOD_RELATED) val isMoodRelated : Boolean,
    @ColumnInfo(name = COLUMN_EVENT_RELATED) val isEventRelated : Boolean,
    @ColumnInfo(name = COLUMN_REASON) val reason : Int,

    @ColumnInfo(name = COLUMN_CURRENT_MAX_CIGARETTES) val currentMaxCigarettes : Int
){
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}
