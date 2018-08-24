package com.sml.data.repository

import com.sml.data.datasource.interpolator.SpeedInterpolator
import com.sml.data.datasource.interpolator.StepsCalculator
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.enums.FileTransferMarker
import com.sml.domain.repository.SpeedTestInterpolatorRepository
import io.reactivex.Observable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SpeedTestInterpolatorRepositoryImpl @Inject constructor(
        private val stepsCalculator: StepsCalculator,
        private val speedInterpolator: SpeedInterpolator
) : SpeedTestInterpolatorRepository {

    companion object {
        const val PERIOD: Long = 50L
    }

    private var previousSpeed: Float = 0.0F

    override fun interpolateSpeed(entity: SpeedTestEntity): Observable<SpeedTestEntity> {
        val speed = speedInterpolator.checkAndCorrectSpeed(entity.currentSpeed, previousSpeed)

        val deltaSpeed = speed - previousSpeed

        val steps = stepsCalculator.calculateSteps(deltaSpeed)

        return Observable.interval(PERIOD, TimeUnit.MILLISECONDS)
                .take(steps)
                .map { rawStep ->
                    val step = rawStep + 1
                    setSpeedToEntity(entity, speedInterpolator.calculateCurrentSpeed(deltaSpeed, step, steps, previousSpeed))
                }
                .doOnComplete { previousSpeed = speed }
    }

    private fun setSpeedToEntity(entity: SpeedTestEntity, inputCurrentSpeed: Float): SpeedTestEntity {
        var downloadSpeed = 0F
        var uploadSpeed = 0F
        var currentSpeed = inputCurrentSpeed

        when (entity.speedTestMode) {
            FileTransferMarker.DOWNLOAD -> downloadSpeed = currentSpeed
            FileTransferMarker.UPLOAD -> uploadSpeed = currentSpeed
            FileTransferMarker.NONE -> {
            }
        }

        return entity.copy(
                downloadSpeed = downloadSpeed,
                uploadSpeed = uploadSpeed,
                currentSpeed = currentSpeed
        )
    }
}