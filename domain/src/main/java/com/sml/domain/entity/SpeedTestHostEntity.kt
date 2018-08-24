package com.sml.domain.entity

data class SpeedTestHostEntity(
        val uid: Int,
        val downloadUrl: String,
        val uploadUrl: String,
        val hostName: String
)