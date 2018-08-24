package com.sml.data.datasource.okhttp

import com.sml.data.datasource.base.SpeedTestListener
import com.sml.data.model.FileTransferModel
import com.sml.data.tools.TimeBenchmark
import com.sml.domain.enums.FileTransferMarker
import okhttp3.MediaType
import okhttp3.ResponseBody
import okio.*
import java.io.IOException

class SpeedTestDownloadResponseBody(
        private val responseBody: ResponseBody,
        private val speedTestListener: SpeedTestListener,
        private val startTimeMillis: Long,
        private val timeBenchmark: TimeBenchmark,
        private val reportInterval: Long
) : ResponseBody() {

    private val bufferedSource = Okio.buffer(initSource(responseBody.source()))

    @Throws(IOException::class)
    override fun contentLength(): Long =
            responseBody.contentLength()

    override fun contentType(): MediaType? =
            responseBody.contentType()

    override fun source(): BufferedSource {
        return bufferedSource
    }

    @Throws(IOException::class)
    private fun initSource(source: Source): Source =
            object : ForwardingSource(source) {
                var totalBytesRead: Long = 0L

                override fun read(sink: Buffer, byteCount: Long): Long {
                    var bytesRead = 0L
                    try {
                        bytesRead = super.read(sink, byteCount)
                        totalBytesRead += if (bytesRead != -1L) bytesRead else 0

                        if (bytesRead == -1L) {
                            speedTestListener.onNext(getZeroModel(bytesRead == -1L))
                            speedTestListener.onComplete()
                        }

                        if (timeBenchmark.checkExpiredTime(reportInterval))
                            speedTestListener.onNext(FileTransferModel(
                                    totalBytesRead = totalBytesRead,
                                    contentLength = responseBody.contentLength(),
                                    isDone = bytesRead == -1L,
                                    startTimeMillis = startTimeMillis,
                                    fileTransferMarker = FileTransferMarker.DOWNLOAD))

                    } catch (e: Exception) {
                        speedTestListener.onNext(getZeroModel())
                        throw e
                    }
                    return bytesRead
                }
            }

    private fun getZeroModel(isDone: Boolean = false): FileTransferModel =
            FileTransferModel(0, 0, isDone, startTimeMillis, FileTransferMarker.NONE)

}