package com.armanmaurya.internetradio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.armanmaurya.internetradio.data.model.Country

@Entity(tableName = "countries")
data class CountryEntity(
    @PrimaryKey val isoCode: String,
    val name: String,
    val stationCount: Int
)

fun CountryEntity.toDomain() = Country(
    name = name,
    isoCode = isoCode,
    stationCount = stationCount
)

fun Country.toEntity() = CountryEntity(
    isoCode = isoCode,
    name = name,
    stationCount = stationCount
)
