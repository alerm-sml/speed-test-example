package com.sml.data.datasource.interpolator

import javax.inject.Inject
import kotlin.math.abs

class StepsCalculator @Inject constructor() {

    companion object {
        const val STEPS_TO_1_MB: Long = 8L
        const val MIN_STEPS: Long = STEPS_TO_1_MB
        const val MAX_STEPS: Long = 100L
    }

    fun calculateSteps(deltaSpeed: Float): Long {
        val steps = (abs(deltaSpeed) * STEPS_TO_1_MB).toLong()
        return checkAndCorrectSteps(steps)
    }

    private fun checkAndCorrectSteps(uncheckedSteps: Long): Long {
        var resultSteps = if (uncheckedSteps < MIN_STEPS) MIN_STEPS else uncheckedSteps
        resultSteps = if (resultSteps > MAX_STEPS) MAX_STEPS else resultSteps
        return resultSteps
    }
}