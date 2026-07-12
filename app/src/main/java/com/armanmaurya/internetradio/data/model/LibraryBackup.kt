package com.armanmaurya.internetradio.data.model

import com.armanmaurya.internetradio.data.local.entity.LibraryStationEntity

enum class ConflictStrategy {
    SKIP,
    OVERWRITE,
    KEEP_NEWER
}

data class LibraryBackup(
    val schemaVersion: Int = 1,
    val exportedAt: String = "",
    val appVersion: String = "",
    val stations: List<LibraryStationEntity>? = null
)
