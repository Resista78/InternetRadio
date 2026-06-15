package com.armanmaurya.internetradio.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.armanmaurya.internetradio.data.local.entity.UserStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserStationDao {
    @Query("SELECT * FROM user_stations ORDER BY addedAt DESC")
    fun getAllUserStations(): Flow<List<UserStationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserStation(station: UserStationEntity)

    @Query("DELETE FROM user_stations WHERE stationUuid = :stationUuid")
    suspend fun deleteUserStation(stationUuid: String)
}
