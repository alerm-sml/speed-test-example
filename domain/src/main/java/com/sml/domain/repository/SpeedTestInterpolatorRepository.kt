package com.sml.domain.repository

import com.sml.domain.entity.SpeedTestEntity
import io.reactivex.Observable

interface SpeedTestInterpolatorRepository {
    fun interpolateSpeed(entity: SpeedTestEntity): Observable<SpeedTestEntity>
}