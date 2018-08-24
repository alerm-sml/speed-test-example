package com.sml.stp.di.modules

import android.arch.persistence.room.Room
import android.content.Context
import com.sml.data.db.AppDatabase
import com.sml.data.db.StpDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DbModule {

    @Module
    companion object {
        private const val DB_NAME = "SpeedTestProjectDataBase"

        @Singleton
        @Provides
        @JvmStatic
        fun provideDb(context: Context): AppDatabase =
                Room.databaseBuilder(context, AppDatabase::class.java, DB_NAME)
                        .build()

        @Singleton
        @Provides
        @JvmStatic
        fun provideDao(database: AppDatabase): StpDao =
                database.stpDao()
    }
}