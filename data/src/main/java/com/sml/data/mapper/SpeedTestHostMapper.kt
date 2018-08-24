package com.sml.data.mapper

import com.sml.data.model.db.SpeedTestHostDbModel
import com.sml.data.model.response.SpeedTestServersResponse
import com.sml.domain.entity.SpeedTestHostEntity
import javax.inject.Inject

class SpeedTestHostMapper @Inject constructor() {

    fun mapToDomain(model: SpeedTestHostDbModel): SpeedTestHostEntity =
            SpeedTestHostEntity(
                    uid = model.uid,
                    downloadUrl = model.downloadUrl,
                    uploadUrl = model.uploadUrl,
                    hostName = model.hostName
            )

    fun mapCollectionToDb(response: SpeedTestServersResponse): List<SpeedTestHostDbModel> {
        val speedTestDbHosts = mutableListOf<SpeedTestHostDbModel>()
        val hosts = response.hosts
        hosts.forEach {
            speedTestDbHosts.add(
                    SpeedTestHostDbModel(
                            downloadUrl = it.downloadUrl,
                            uploadUrl = it.uploadUrl,
                            hostName = it.hostName
                    )
            )
        }
        return speedTestDbHosts.toList()
    }
}