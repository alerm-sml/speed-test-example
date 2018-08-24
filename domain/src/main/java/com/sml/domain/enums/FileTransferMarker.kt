package com.sml.domain.enums

enum class FileTransferMarker(val mode: Int) {
    NONE(0),
    DOWNLOAD(1),
    UPLOAD(2),
}