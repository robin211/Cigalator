package com.dputera.cigalator.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [CigaretteTime::class], version = 1)
abstract class CigaretteTimeDB: RoomDatabase() {
    abstract fun cigaretteTimeDao() : CigaretteTimeDAO

    companion object {
        private var instance: CigaretteTimeDB? = null

        @Synchronized
        fun getInstance(ctx: Context): CigaretteTimeDB {
            if(instance == null)
                instance = Room.databaseBuilder(ctx.applicationContext, CigaretteTimeDB::class.java,
                    DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            return instance!!

        }

    }
}