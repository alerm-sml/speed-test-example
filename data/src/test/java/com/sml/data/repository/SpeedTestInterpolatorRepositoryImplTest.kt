package com.sml.data.repository

import com.sml.data.RxSchedulesRule
import com.sml.data.datasource.interpolator.SpeedInterpolator
import com.sml.data.datasource.interpolator.StepsCalculator
import com.sml.domain.entity.SpeedTestEntity
import io.reactivex.observers.TestObserver
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class SpeedTestInterpolatorRepositoryImplTest {

    @get:Rule
    val rxSchedulesRule = RxSchedulesRule()

    private lateinit var repository: SpeedTestInterpolatorRepositoryImpl
    private val stepsCalculator: StepsCalculator = mock(StepsCalculator::class.java)
    private val speedInterpolator: SpeedInterpolator = mock(SpeedInterpolator::class.java)
    private val entity: SpeedTestEntity = mock(SpeedTestEntity::class.java)

    @Before
    fun setUp() {
        repository = SpeedTestInterpolatorRepositoryImpl(stepsCalculator, speedInterpolator)
    }

    @Test
    fun interpolateSpeed() {
        val testObserver: TestObserver<SpeedTestEntity> = repository.interpolateSpeed(entity).test()

        testObserver
                .assertSubscribed()
                .assertComplete()
                .assertNoErrors()
    }
}