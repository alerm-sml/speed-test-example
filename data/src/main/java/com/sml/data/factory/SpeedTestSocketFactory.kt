package com.sml.data.factory

import fr.bmartel.speedtest.SpeedTestSocket
import javax.inject.Inject

class SpeedTestSocketFactory @Inject constructor() {

    fun build(): SpeedTestSocket =
            SpeedTestSocket()
}