package com.sml.stp.format

import javax.inject.Inject

class SpeedTestFormat @Inject constructor() {

    fun format(input: Float): Float =
            Math.round(input * 100.0F) / 100.0F
}