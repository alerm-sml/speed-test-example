package com.sml.domain.repository

import com.sml.domain.entity.SpeedTestHostEntity
import io.reactivex.Single

interface SpeedTestHostRepository {

    fun getAllSpeedTestHosts(): Single<List<SpeedTestHostEntity>>
}