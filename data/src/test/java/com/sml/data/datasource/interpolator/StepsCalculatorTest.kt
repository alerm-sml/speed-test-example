package com.sml.data.datasource.interpolator


import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class StepsCalculatorTest {

    private lateinit var stepsCalculator: StepsCalculator

    @Before
    fun setUp() {
        stepsCalculator = StepsCalculator()
    }

    @Test
    fun calculateSteps() {
        assertEquals(stepsCalculator.calculateSteps(50F), StepsCalculator.MAX_STEPS)
        assertEquals(stepsCalculator.calculateSteps(0F), StepsCalculator.MIN_STEPS)
        assertEquals(stepsCalculator.calculateSteps(5F), 40L)
        assertEquals(stepsCalculator.calculateSteps(3F), 24L)
        assertEquals(stepsCalculator.calculateSteps(3.33F), 26L)
        assertEquals(stepsCalculator.calculateSteps(-100000F), StepsCalculator.MAX_STEPS)
        assertEquals(stepsCalculator.calculateSteps(10000000F), StepsCalculator.MAX_STEPS)
    }
}