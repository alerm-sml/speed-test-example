package com.sml.data.mapper

import com.sml.data.model.db.SpeedTestHostDbModel
import com.sml.data.model.network.SpeedTestHostNwModel
import com.sml.domain.entity.SpeedTestHostEntity
import javax.inject.Inject

class SpeedTestHostMapper @Inject constructor() {

    fun mapToDomain(model: SpeedTestHostDbModel): SpeedTestHostEntity =
            SpeedTestHostEntity(
                    uid = model.uid,
                    hostName = model.hostName,
                    downloadUrl = model.downloadUrl,
                    uploadUrl = model.uploadUrl
            )

    fun mapToDb(nwModel: SpeedTestHostNwModel): SpeedTestHostDbModel =
            SpeedTestHostDbModel(
                    hostName = nwModel.hostName,
                    downloadUrl = nwModel.downloadUrl,
                    uploadUrl = nwModel.uploadUrl
            )
}