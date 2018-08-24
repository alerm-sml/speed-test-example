package com.sml.domain.repository

import com.sml.domain.entity.SpeedTestEntity
import io.reactivex.Observable

interface SpeedTestDownloadRepository {
    fun measureDownloadSpeed(speedTestEntity: SpeedTestEntity): Observable<SpeedTestEntity>
}