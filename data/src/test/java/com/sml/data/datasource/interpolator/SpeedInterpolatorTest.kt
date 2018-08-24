package com.sml.data.datasource.interpolator

import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class SpeedInterpolatorTest {

    private lateinit var speedInterpolator: SpeedInterpolator
    private lateinit var stepsCalculator: StepsCalculator

    @Before
    fun setUp() {
        speedInterpolator = SpeedInterpolator()
        stepsCalculator = StepsCalculator()
    }

    // calculateCurrentSpeed():
    @Test
    fun calculateCurrentSpeedCheckMix() {
        val previousSpeed = 10000.0F
        val speed = -7.0F
        val deltaSpeed: Float = speed - previousSpeed
        val steps = stepsCalculator.calculateSteps(deltaSpeed)
        val step = steps
        assertEquals(speedInterpolator.calculateCurrentSpeed(deltaSpeed, step, steps, previousSpeed), SpeedInterpolator.MIN_SPEED)
    }

    @Test
    fun calculateCurrentSpeedCheckMax() {
        val previousSpeed = -10000.0F
        val speed = 10000.0F
        val deltaSpeed: Float = speed - previousSpeed
        val steps = stepsCalculator.calculateSteps(deltaSpeed)
        val step = steps
        assertEquals(speedInterpolator.calculateCurrentSpeed(deltaSpeed, step, steps, previousSpeed), SpeedInterpolator.MAX_SPEED)
    }

    @Test
    fun calculateCurrentSpeedMinus10000To7() {
        val previousSpeed = -10000.0F
        val speed = 7.0F
        val deltaSpeed: Float = speed - previousSpeed
        val steps = stepsCalculator.calculateSteps(deltaSpeed)
        val step = steps
        assertEquals(speedInterpolator.calculateCurrentSpeed(deltaSpeed, step, steps, previousSpeed), speed)
    }

    @Test
    fun calculateCurrentSpeedCheckForeach() {
        foreachCalculateCurrentSpeedChecker(previousSpeed = 1.0F, speed = 0.0F)
        foreachCalculateCurrentSpeedChecker(previousSpeed = 1.0F, speed = 7.0F)
        foreachCalculateCurrentSpeedChecker(previousSpeed = 0.7806066F, speed = 7.0F)
        foreachCalculateCurrentSpeedChecker(previousSpeed = 3.0837257F, speed = 0.0F)
    }

    private fun foreachCalculateCurrentSpeedChecker(previousSpeed: Float, speed: Float) {
        val deltaSpeed: Float = speed - previousSpeed
        val steps = stepsCalculator.calculateSteps(deltaSpeed)
        val speedByStep = deltaSpeed / steps
        for (step in 1..steps) {
            assertEquals(speedInterpolator.calculateCurrentSpeed(deltaSpeed, step, steps, previousSpeed), previousSpeed + step * speedByStep, 0.01F)
        }
    }

    // checkAndCorrectSpeed():
    @Test
    fun checkAndCorrectSpeed() {
        assertEquals(speedInterpolator.checkAndCorrectSpeed(0F, 1F), 0F)
        assertEquals(speedInterpolator.checkAndCorrectSpeed(-100F, 1F), 0F)
        assertEquals(speedInterpolator.checkAndCorrectSpeed(110F, 1F), 100F)
        assertEquals(speedInterpolator.checkAndCorrectSpeed(5F, 5F), 5F - SpeedInterpolator.DECREASE_SPEED_VALUE)
        assertEquals(speedInterpolator.checkAndCorrectSpeed(0F, 0F), 0F)
    }
}