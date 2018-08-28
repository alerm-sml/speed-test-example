package com.sml.domain.repository

import com.sml.domain.entity.SpeedTestEntity
import io.reactivex.Single

interface SpeedTestHostRepository {
    fun getSpeedTestHost(): Single<SpeedTestEntity>
}