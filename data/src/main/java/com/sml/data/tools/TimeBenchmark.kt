package com.sml.data.tools

import javax.inject.Inject

class TimeBenchmark @Inject constructor() {

    private var startPeriodTime: Long = 0

    fun build(): Long =
            System.currentTimeMillis()

    fun checkExpiredTime(reportInterval: Long): Boolean {
        val currentTime = build()
        val delta = currentTime - startPeriodTime
        if (delta >= reportInterval) {
            startPeriodTime = currentTime
            return true
        }
        return false
    }
}