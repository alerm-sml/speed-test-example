package com.sml.data.model

import com.sml.domain.enums.FileTransferMarker

data class FileTransferModel(
        val totalBytesRead: Long,
        val contentLength: Long,
        val isDone: Boolean,
        val startTimeMillis: Long,
        val fileTransferMarker: FileTransferMarker
)