package com.sml.data.datasource.interpolator

import javax.inject.Inject

class SpeedInterpolator @Inject constructor() {

    companion object {
        const val MIN_SPEED: Float = 0.0f
        const val MAX_SPEED: Float = 100.0f
        const val DECREASE_SPEED_VALUE: Float = 0.015f
    }

    fun calculateCurrentSpeed(deltaSpeed: Float, step: Long, steps: Long, previousSpeed: Float): Float {
        val stepValue = deltaSpeed * step / steps
        val currentSpeed = previousSpeed + stepValue
        return checkAndCorrectSpeed(currentSpeed, previousSpeed)
    }

    fun checkAndCorrectSpeed(uncheckedSpeed: Float, previousSpeed: Float): Float {
        val resultSpeed = checkAndCorrectSpeedIfSameSpeed(uncheckedSpeed, previousSpeed)
        return checkAndCorrectSpeedMinMax(resultSpeed)
    }

    private fun checkAndCorrectSpeedMinMax(uncheckedSpeed: Float): Float {
        var resultSpeed = if (uncheckedSpeed < MIN_SPEED) MIN_SPEED else uncheckedSpeed
        resultSpeed = if (resultSpeed > MAX_SPEED) MAX_SPEED else resultSpeed
        return resultSpeed
    }

    private fun checkAndCorrectSpeedIfSameSpeed(currentSpeed: Float, previousSpeed: Float): Float {
        if (currentSpeed != previousSpeed) return currentSpeed
        val tmpSpeed = currentSpeed - DECREASE_SPEED_VALUE
        return if (tmpSpeed < 0F) 0F else tmpSpeed
    }
}