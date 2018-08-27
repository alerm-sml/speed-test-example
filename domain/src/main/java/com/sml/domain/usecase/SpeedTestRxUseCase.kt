package com.sml.domain.usecase

import com.sml.domain.development.Logger
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.enums.FileTransferMarker
import com.sml.domain.helper.DomainConst
import com.sml.domain.repository.*
import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SpeedTestRxUseCase @Inject constructor(
        private val logger: Logger,
        private val speedTestHostRepository: SpeedTestHostRepository,
        private val speedTestLatencyRepository: ServerLatencyRepository,
        private val speedTestUploadRepository: SpeedTestUploadRepository,
        private val speedTestDownloadRepository: SpeedTestDownloadRepository,
        private val speedTestInterpolatorRepository: SpeedTestInterpolatorRepository
) {

    fun execute(): Observable<SpeedTestEntity> =
            startSpeedTest()
                    .concatMap { speedTestInterpolatorRepository.interpolateSpeed(it) }
                    .doOnNext { logger.d("${it.speedTestMode.name} speed ${it.currentSpeed}") }

    private fun startSpeedTest(): Observable<SpeedTestEntity> =
            speedTestHostRepository.getAllSpeedTestHosts()
                    .flatMap { speedTestLatencyRepository.getHostModelWithMinLatencySingle(it) }
                    .flatMapObservable { entity ->
                        Observable.concat(
                                runMeasureDownloadSpeed(entity),
                                runMeasureUploadSpeed(entity)
                        )
                    }

    private fun runMeasureDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            getTimer().withLatestFrom(
                    measureDownloadSpeed(speedTestEntity),
                    BiFunction<Long, SpeedTestEntity, SpeedTestEntity> { _, entity -> entity }
            ).concatWith(Observable.just(returnLastEmit(speedTestEntity)))

    private fun runMeasureUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            getTimer().withLatestFrom(
                    measureUploadSpeed(speedTestEntity),
                    BiFunction<Long, SpeedTestEntity, SpeedTestEntity> { _, entity -> entity }
            ).concatWith(Observable.just(returnLastEmit(speedTestEntity)))

    private fun measureDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            speedTestDownloadRepository.measureDownloadSpeed(speedTestEntity)

    private fun measureUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            speedTestUploadRepository.measureUploadSpeed(speedTestEntity)

    private fun getTimer(): Observable<Long> =
            Observable.interval(DomainConst.REPORT_INTERVAL, TimeUnit.MILLISECONDS)
                    .take(DomainConst.FIXED_TIME / DomainConst.REPORT_INTERVAL)

    private fun returnLastEmit(speedTestEntity: SpeedTestEntity): SpeedTestEntity {
        return speedTestEntity.copy(
                currentSpeed = 0F,
                speedTestMode = FileTransferMarker.NONE
        )
    }
}