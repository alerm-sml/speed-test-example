package com.sml.data.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import com.sml.data.model.db.SpeedTestHostDbModel

@Dao
interface StpDao {

    /*=== SPEEDTEST HOSTS ===*/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSpeedtestHosts(list: List<SpeedTestHostDbModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSpeedtestHost(model: SpeedTestHostDbModel)

    @Query("DELETE FROM speed_test_host")
    fun deleteAllSpeedtestHosts()

    @Query("SELECT * FROM speed_test_host ")
    fun getSpeedTestHosts(): List<SpeedTestHostDbModel>
}