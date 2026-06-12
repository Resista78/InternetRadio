package com.armanmaurya.internetradio.data.repository

import com.armanmaurya.internetradio.data.local.dao.RecentStationDao
import com.armanmaurya.internetradio.data.local.entity.toDomain
import com.armanmaurya.internetradio.data.local.entity.toRecentEntity
import com.armanmaurya.internetradio.data.model.RadioStation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecentRepository @Inject constructor(
    private val recentStationDao: RecentStationDao
) {
    fun getAllRecent(): Flow<List<RadioStation>> =
        recentStationDao.getAllRecent().map { entities ->
            entities.map { it.toDomain() }
        }

    suspend fun addRecentStation(station: RadioStation) {
        recentStationDao.insertOrUpdate(station.toRecentEntity())
    }

    suspend fun removeRecent(stationUuid: String) {
        recentStationDao.deleteRecent(stationUuid)
    }

    suspend fun clearAllRecent() {
        recentStationDao.clearAllRecent()
    }
}
