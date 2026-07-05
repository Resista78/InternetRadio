package com.armanmaurya.internetradio.data.local.dao

import androidx.room.*
import com.armanmaurya.internetradio.data.local.entity.LibraryStationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface LibraryStationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStation(station: LibraryStationEntity)

    @Query("DELETE FROM library_stations WHERE stationUuid = :stationUuid")
    suspend fun deleteStationById(stationUuid: String)

    @Query("SELECT * FROM library_stations ORDER BY addedAt DESC")
    fun getAllStations(): Flow<List<LibraryStationEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM library_stations WHERE stationUuid = :stationUuid)")
    fun isStationInLibrary(stationUuid: String): Flow<Int>

    @Query("SELECT EXISTS(SELECT 1 FROM library_stations WHERE stationUuid = :stationUuid)")
    suspend fun isStationInLibraryDirect(stationUuid: String): Boolean

    @Query("SELECT * FROM library_stations WHERE stationUuid = :stationUuid")
    suspend fun getStationById(stationUuid: String): LibraryStationEntity?
}
