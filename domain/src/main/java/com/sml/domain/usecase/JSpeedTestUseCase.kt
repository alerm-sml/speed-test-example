package com.sml.domain.usecase

import com.sml.domain.development.Logger
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.repository.SpeedTestDownloadRepository
import com.sml.domain.repository.SpeedTestHostRepository
import com.sml.domain.repository.SpeedTestInterpolatorRepository
import com.sml.domain.repository.SpeedTestUploadRepository
import io.reactivex.Observable
import javax.inject.Inject

class JSpeedTestUseCase @Inject constructor(
        private val logger: Logger,
        private val speedTestHostRepository: SpeedTestHostRepository,
        private val speedTestUploadRepository: SpeedTestUploadRepository,
        private val speedTestDownloadRepository: SpeedTestDownloadRepository,
        private val speedTestInterpolatorRepository: SpeedTestInterpolatorRepository
) {

    fun execute(): Observable<SpeedTestEntity> =
            startSpeedTest()
                    .concatMap { speedTestInterpolatorRepository.interpolateSpeed(it) }
                    .doOnNext { logger.d("${it.speedTestMode.name} speed ${it.currentSpeed}") }

    private fun startSpeedTest(): Observable<SpeedTestEntity> =
            speedTestHostRepository.getSpeedTestHost()
                    .flatMapObservable {
                        Observable.concat(
                                speedTestDownloadRepository.measureDownloadSpeed(it),
                                speedTestUploadRepository.measureUploadSpeed(it)
                        )
                    }
}