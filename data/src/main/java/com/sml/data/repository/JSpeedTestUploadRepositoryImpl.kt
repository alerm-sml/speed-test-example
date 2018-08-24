package com.sml.data.repository

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.datasource.jspeedtest.JSpeedTestApiUpload
import com.sml.data.mapper.SpeedTestEntityMapper
import com.sml.data.model.FileTransferModel
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.repository.SpeedTestUploadRepository
import io.reactivex.Observable
import javax.inject.Inject

class JSpeedTestUploadRepositoryImpl @Inject constructor(
        private val jSpeedTestApiUpload: JSpeedTestApiUpload,
        private val speedTestEntityMapper: SpeedTestEntityMapper
) : SpeedTestUploadRepository {

    private fun runUploadSpeedTest(url: String): Observable<FileTransferModel> =
            Observable.create {
                jSpeedTestApiUpload.initUploadSpeedtestSettings(
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
                it.setCancellable { jSpeedTestApiUpload.stopTask() }
                jSpeedTestApiUpload.runUploadSpeedtest(url)
            }

    override fun measureUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            runUploadSpeedTest(speedTestEntity.uploadUrl)
                    .map { speedTestEntityMapper.mapToDomain(speedTestEntity, it) }
}