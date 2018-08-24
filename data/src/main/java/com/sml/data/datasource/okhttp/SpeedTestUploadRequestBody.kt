package com.sml.data.datasource.okhttp

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.model.FileTransferModel
import com.sml.data.tools.TimeBenchmark
import com.sml.domain.enums.FileTransferMarker
import okhttp3.MediaType
import okhttp3.RequestBody
import okio.BufferedSink
import okio.Okio
import java.io.File

class SpeedTestUploadRequestBody(
        private val file: File,
        private val contentType: String,
        private val speedTestListener: SpeedTestListener,
        private val timeBenchmark: TimeBenchmark,
        private val reportInterval: Long
) : RequestBody() {

    private val startTimeMillis: Long = timeBenchmark.build()

    companion object {
        const val OKIO_SEGMENT_SIZE = 2048L
    }

    override fun contentType(): MediaType? =
            MediaType.parse(contentType)

    override fun writeTo(sink: BufferedSink) {
        Okio.source(file).use { it ->
            var totalBytesRead: Long = 0
            try {
                while (true) {
                    val read = it.read(sink.buffer(), OKIO_SEGMENT_SIZE)
                    if (read == -1L) {
                        speedTestListener.onNext(getZeroModel(read == -1L))
                        speedTestListener.onComplete()
                        break
                    }
                    totalBytesRead += read
                    sink.flush()
                    if (timeBenchmark.checkExpiredTime(reportInterval))
                        speedTestListener.onNext(FileTransferModel(
                                totalBytesRead = totalBytesRead,
                                contentLength = this.contentLength(),
                                isDone = read == -1L,
                                startTimeMillis = startTimeMillis,
                                fileTransferMarker = FileTransferMarker.UPLOAD))
                }
            } catch (e: Exception) {
                speedTestListener.onNext(getZeroModel())
                throw e
            }
        }

    }

    private fun getZeroModel(isDone: Boolean = false): FileTransferModel =
            FileTransferModel(0, 0, isDone, startTimeMillis, FileTransferMarker.NONE)
}