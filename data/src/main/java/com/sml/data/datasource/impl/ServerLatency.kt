package com.sml.data.datasource.impl

import com.sml.data.tools.TimeBenchmark
import com.sml.domain.helper.DomainConst
import timber.log.Timber
import java.io.IOException
import java.net.InetAddress
import javax.inject.Inject

class ServerLatency @Inject constructor(
        private val timeBenchmark: TimeBenchmark
) {

    fun getAverageLatencyToHost(host: String, timeOut: Int = 1500, howMoneyTimes: Int = 1): Long {
        val time = arrayOfNulls<Long>(howMoneyTimes)
        var latencySum = 0L

        for (i in 0 until time.size) {
            val startTime = timeBenchmark.build()

            var isReachable = false
            try {
                isReachable = InetAddress.getByName(host).isReachable(timeOut)
            } catch (e: IOException) {
                return DomainConst.MAX_LATENCY
            }

            val endTime = timeBenchmark.build()

            val deltaTime = endTime - startTime
            Timber.d("startTime $startTime endTime $endTime delta $deltaTime")
            time[i] = deltaTime
            latencySum += deltaTime
        }

        return latencySum / howMoneyTimes
    }
}