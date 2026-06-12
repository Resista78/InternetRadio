package com.armanmaurya.internetradio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.armanmaurya.internetradio.data.model.Language

@Entity(tableName = "languages")
data class LanguageEntity(
    @PrimaryKey val name: String,
    val isoCode: String?,
    val stationCount: Int
)

fun LanguageEntity.toDomain() = Language(
    name = name,
    isoCode = isoCode,
    stationCount = stationCount
)

fun Language.toEntity() = LanguageEntity(
    name = name,
    isoCode = isoCode,
    stationCount = stationCount
)
