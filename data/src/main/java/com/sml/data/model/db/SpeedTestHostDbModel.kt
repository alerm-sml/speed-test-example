package com.sml.data.model.db

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "speed_test_host")
data class SpeedTestHostDbModel(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "uid") val uid: Int = 0,
        @ColumnInfo(name = "download_url") val downloadUrl: String,
        @ColumnInfo(name = "upload_url") val uploadUrl: String,
        @ColumnInfo(name = "hostname") val hostName: String
)