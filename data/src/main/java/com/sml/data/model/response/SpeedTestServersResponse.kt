package com.sml.data.model.response

import com.google.gson.annotations.SerializedName
import com.sml.data.model.network.SpeedtestHostNwModel

data class SpeedTestServersResponse(
        @SerializedName("hosts") val hosts: List<SpeedtestHostNwModel>,
        @SerializedName("status") val status: String
)