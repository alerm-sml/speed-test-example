package com.sml.data.repository

import com.sml.data.db.StpDao
import com.sml.data.mapper.SpeedTestHostMapper
import com.sml.data.model.db.SpeedTestHostDbModel
import com.sml.data.network.MockStpApi
import com.sml.data.network.StpApi
import com.sml.domain.entity.SpeedTestHostEntity
import com.sml.domain.repository.SpeedTestHostRepository
import io.reactivex.Single
import javax.inject.Inject

class SpeedTestHostRepositoryImpl @Inject constructor(
        private val api: StpApi,
        private val mockStpApi: MockStpApi,
        private val dao: StpDao,
        private val speedTestHostMapper: SpeedTestHostMapper
) : SpeedTestHostRepository {

    override fun getAllSpeedTestHosts(): Single<List<SpeedTestHostEntity>> =
            getSpeedTestHostsFromNetwork()
                    .map { dao.getSpeedTestHosts() }
                    .map { it.map { model -> speedTestHostMapper.mapToDomain(model) } }

    private fun getSpeedTestHostsFromNetwork(): Single<List<SpeedTestHostDbModel>> =
            mockStpApi.getSpeedTestServers()
                    .doOnSubscribe { dao.deleteAllSpeedtestHosts() }
                    .doOnSuccess {
                        dao.insertAllSpeedtestHosts(speedTestHostMapper.mapCollectionToDb(it))
                    }
                    .map { speedTestHostMapper.mapCollectionToDb(it) }
}