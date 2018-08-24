package com.sml.data.repository

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.datasource.jspeedtest.JSpeedTestApiDownload
import com.sml.data.mapper.SpeedTestEntityMapper
import com.sml.data.model.FileTransferModel
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.repository.SpeedTestDownloadRepository
import io.reactivex.Observable
import javax.inject.Inject

class JSpeedTestDownloadRepositoryImpl @Inject constructor(
        private val jSpeedTestApiDownload: JSpeedTestApiDownload,
        private val speedTestEntityMapper: SpeedTestEntityMapper
) : SpeedTestDownloadRepository {

    private fun runDownloadSpeedTest(url: String): Observable<FileTransferModel> =
            Observable.create {
                jSpeedTestApiDownload.initDownloadSpeedtestSettings(
                        object : SpeedTestListener {
                            override fun onComplete() {
                                it.onComplete()
                            }

                            override fun onNext(model: FileTransferModel) {
                                it.onNext(model)
                            }

                            override fun onError(throwable: Throwable) {
                                it.onError(throwable)
                            }
                        }
                )
                it.setCancellable { jSpeedTestApiDownload.stopTask() }
                jSpeedTestApiDownload.runDownloadSpeedtest(url)
            }

    override fun measureDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            runDownloadSpeedTest(speedTestEntity.downloadUrl)
                    .map { speedTestEntityMapper.mapToDomain(speedTestEntity, it) }
}