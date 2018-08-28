package com.sml.data.repository

import com.sml.data.db.StpDao
import com.sml.data.mapper.SpeedTestEntityMapper
import com.sml.data.mapper.SpeedTestHostMapper
import com.sml.data.network.MockStpApi
import com.sml.data.network.StpApi
import com.sml.domain.entity.SpeedTestEntity
import com.sml.domain.repository.SpeedTestHostRepository
import io.reactivex.Single
import javax.inject.Inject

class SpeedTestHostRepositoryImpl @Inject constructor(
        private val api: StpApi,
        private val mockStpApi: MockStpApi,
        private val dao: StpDao,
        private val speedTestHostMapper: SpeedTestHostMapper,
        private val speedTestEntityMapper: SpeedTestEntityMapper
) : SpeedTestHostRepository {

    override fun getSpeedTestHost(): Single<SpeedTestEntity> =
            mockStpApi.getSpeedTestServer()
                    .map { speedTestHostMapper.mapToDb(it) }
                    .doOnSuccess { dao.insertSpeedtestHost(it) }
                    .map { speedTestHostMapper.mapToDomain(it) }
                    .map { speedTestEntityMapper.mapHostToDomain(it) }
}