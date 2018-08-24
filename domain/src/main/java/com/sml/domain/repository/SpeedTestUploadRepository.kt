package com.sml.domain.repository

import com.sml.domain.entity.SpeedTestEntity
import io.reactivex.Observable

interface SpeedTestUploadRepository {
    fun measureUploadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity>
}