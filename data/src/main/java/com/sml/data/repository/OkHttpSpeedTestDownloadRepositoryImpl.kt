package com.sml.data.repository

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.datasource.okhttp.OkHttpApiSpeedTestDownload
import com.sml.data.mapper.SpeedTestEntityMapper
import com.sml.data.model.FileTransferModel
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.repository.SpeedTestDownloadRepository
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.InterruptedIOException
import javax.inject.Inject

class OkHttpSpeedTestDownloadRepositoryImpl @Inject constructor(
        private val okHttpApiSpeedTestDownload: OkHttpApiSpeedTestDownload,
        private val speedTestEntityMapper: SpeedTestEntityMapper
) : SpeedTestDownloadRepository {

    override fun measureDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            initMeasuredDownloadSpeed(speedTestEntity)
                    .doOnSubscribe { Timber.d("DOWNLOAD start") }
                    .subscribeOn(Schedulers.io())

    private fun initMeasuredDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            Observable.create<FileTransferModel> {
                try {
                    runDownloadSpeedTest(speedTestEntity = speedTestEntity, emitter = it)
                } catch (e: Exception) {
                    if (e !is InterruptedIOException)
                        it.tryOnError(e)
                }
                it.setCancellable { okHttpApiSpeedTestDownload.stopTask() }
            }.map { speedTestEntityMapper.mapToDomain(speedTestEntity, it) }

    private fun runDownloadSpeedTest(speedTestEntity: SpeedTestEntity, emitter: ObservableEmitter<FileTransferModel>) =
            okHttpApiSpeedTestDownload.executeDownloadSpeedTest(
                    url = speedTestEntity.downloadUrl,
                    speedTestListener = object : SpeedTestListener {
                        override fun onComplete() {
                            emitter.onComplete()
                        }

                        override fun onNext(model: FileTransferModel) {
                            emitter.onNext(model)
                        }

                        override fun onError(throwable: Throwable) {
                            emitter.onError(throwable)
                        }
                    }
            )
}