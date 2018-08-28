package com.sml.data.repository

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.datasource.okhttp.OkHttpApiSpeedTestUpload
import com.sml.data.datasource.okhttp.OkHttpWayConst
import com.sml.data.mapper.SpeedTestEntityMapper
import com.sml.data.model.FileTransferModel
import com.sml.data.tools.FileTools
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.repository.SpeedTestUploadRepository
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class OkHttpSpeedTestUploadRepositoryImpl @Inject constructor(
        private val okHttpApiSpeedTestUpload: OkHttpApiSpeedTestUpload,
        private val speedTestEntityMapper: SpeedTestEntityMapper,
        private val fileTools: FileTools
) : SpeedTestUploadRepository {

    override fun measureUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            initMeasuredUploadSpeed(speedTestEntity)
                    .doOnSubscribe { Timber.d("UPLOAD start") }
                    .subscribeOn(Schedulers.io())

    private fun initMeasuredUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity> =
            Observable.create<FileTransferModel> {
                try {
                    runUploadSpeedTest(speedTestEntity = speedTestEntity, emitter = it)
                } catch (e: Exception) {
                    it.tryOnError(e)
                }
                it.setCancellable { okHttpApiSpeedTestUpload.stopTask() }
            }.map { speedTestEntityMapper.mapToDomain(speedTestEntity, it) }

    private fun runUploadSpeedTest(speedTestEntity: SpeedTestEntity, emitter: ObservableEmitter<FileTransferModel>) =
            okHttpApiSpeedTestUpload.executeUploadSpeedTest(
                    url = speedTestEntity.uploadUrl,
                    speedTestListener = object : SpeedTestListener {
                        override fun onComplete() {
                            emitter.onComplete()
                        }

                        override fun onNext(model: FileTransferModel) {
                            emitter.onNext(model)
                        }

                        override fun onError(throwable: Throwable) {
                            emitter.tryOnError(throwable)
                        }
                    },
                    file = createAndGetDummyFile()
            )

    private fun createAndGetDummyFile(): File =
            fileTools.createFileWithSizeByFileName(OkHttpWayConst.SPEED_TEST_FILE, OkHttpWayConst.SPEED_TEST_FILE_SIZE)
}