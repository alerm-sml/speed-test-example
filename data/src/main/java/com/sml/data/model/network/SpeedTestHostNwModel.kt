package com.sml.data.model.network

import com.google.gson.annotations.SerializedName

data class SpeedTestHostNwModel(
        @SerializedName("hostname") val hostName: String,
        @SerializedName("download_url") val downloadUrl: String,
        @SerializedName("upload_url") val uploadUrl: String
)