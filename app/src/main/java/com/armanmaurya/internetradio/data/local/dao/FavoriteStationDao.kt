package com.armanmaurya.internetradio.data.local.dao

import androidx.room.*
import com.armanmaurya.internetradio.data.local.entity.FavoriteStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(station: FavoriteStationEntity)

    @Query("DELETE FROM favorite_stations WHERE stationUuid = :stationUuid")
    suspend fun deleteFavoriteById(stationUuid: String)

    @Query("SELECT * FROM favorite_stations ORDER BY addedAt DESC")
    fun getAllFavorites(): Flow<List<FavoriteStationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_stations WHERE stationUuid = :stationUuid)")
    fun isFavorite(stationUuid: String): Flow<Int>
}
