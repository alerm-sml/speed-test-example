package com.sml.domain.repository

import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.entity.SpeedTestHostEntity
import io.reactivex.Single

interface ServerLatencyRepository {
    fun getHostModelWithMinLatencySingle(hosts: List<SpeedTestHostEntity>): Single<SpeedTestEntity>
}