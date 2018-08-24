package com.sml.data.repository

import com.sml.data.datasource.impl.ServerLatency
import com.sml.data.mapper.SpeedTestEntityMapper
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.entity.SpeedTestHostEntity
import com.sml.domain.repository.ServerLatencyRepository
import io.reactivex.Single
import java.io.IOException
import javax.inject.Inject

class ServerLatencyRepositoryImpl @Inject constructor(
        private val serverLatency: ServerLatency,
        private val speedTestEntityMapper: SpeedTestEntityMapper
) : ServerLatencyRepository {

    override fun getHostModelWithMinLatencySingle(hosts: List<SpeedTestHostEntity>): Single<SpeedTestEntity> =
            Single.create { emitter ->
                var minLatency = Long.MAX_VALUE
                var minEntity: SpeedTestHostEntity? = null

                hosts.forEach {
                    var tmpLatency = 0L
                    try {
                        tmpLatency = serverLatency.getAverageLatencyToHost(host = it.hostName)
                    } catch (e: IOException) {
                        emitter.tryOnError(e)
                    }

                    if (tmpLatency < minLatency) {
                        minLatency = tmpLatency
                        minEntity = it
                    }
                }

                minEntity?.let {
                    val speedTestEntity = speedTestEntityMapper.mapHostToDomain(it)
                    speedTestEntity.latency = minLatency
                    emitter.onSuccess(speedTestEntity)
                } ?: kotlin.run {
                    emitter.tryOnError(Exception("Speed Test list empty"))
                }
            }
}