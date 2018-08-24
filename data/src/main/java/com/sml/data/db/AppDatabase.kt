package com.sml.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import com.sml.data.model.db.SpeedTestHostDbModel

@Database(
        entities = [
            SpeedTestHostDbModel::class
        ],
        version = 1,
        exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun stpDao(): StpDao
}