package com.armanmaurya.internetradio.data.local.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.DeleteTable
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.AutoMigrationSpec
import com.armanmaurya.internetradio.data.local.dao.FavoriteStationDao
import com.armanmaurya.internetradio.data.local.dao.RecentStationDao
import com.armanmaurya.internetradio.data.local.dao.UserStationDao
import com.armanmaurya.internetradio.data.local.entity.FavoriteStationEntity
import com.armanmaurya.internetradio.data.local.entity.RecentStationEntity
import com.armanmaurya.internetradio.data.local.entity.UserStationEntity

@Database(
    entities = [
        FavoriteStationEntity::class,
        RecentStationEntity::class,
        UserStationEntity::class
    ],
    version = 2,
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = RadioDatabase.Migration1To2Spec::class)
    ]
)
@TypeConverters(Converters::class)
abstract class RadioDatabase : RoomDatabase() {
    
    @DeleteTable(tableName = "countries")
    @DeleteTable(tableName = "languages")
    @DeleteTable(tableName = "tags")
    class Migration1To2Spec : AutoMigrationSpec

    abstract val favoriteStationDao: FavoriteStationDao
    abstract val recentStationDao: RecentStationDao
    abstract val userStationDao: UserStationDao
}
