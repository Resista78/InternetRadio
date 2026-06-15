package com.armanmaurya.internetradio.data.repository

import android.content.Context
import com.armanmaurya.internetradio.data.local.dao.MetadataDao
import com.armanmaurya.internetradio.data.local.entity.toDomain
import com.armanmaurya.internetradio.data.local.entity.toEntity
import com.armanmaurya.internetradio.data.model.Country
import com.armanmaurya.internetradio.data.model.Language
import com.armanmaurya.internetradio.data.model.RadioStation
import com.armanmaurya.internetradio.data.remote.RadioBrowserApi
import com.armanmaurya.internetradio.data.remote.dto.toDomain
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StationRepository @Inject constructor(
    private val api: RadioBrowserApi,
    @ApplicationContext private val context: Context,
    private val metadataDao: MetadataDao,
) {

    suspend fun filterStations(
        name: String? = null,
        nameExact: Boolean? = null,
        country: String? = null,
        countryExact: Boolean? = null,
        countryCode: String? = null,
        state: String? = null,
        stateExact: Boolean? = null,
        language: String? = null,
        languageExact: Boolean? = null,
        tag: String? = null,
        tagExact: Boolean? = null,
        tagList: String? = null,
        codec: String? = null,
        bitrateMin: Int? = null,
        bitrateMax: Int? = null,
        isHttps: Boolean? = null,
        order: String = "votes",
        reverse: Boolean = true,
        limit: Int = 40,
        offset: Int = 0,
        hideBroken: Boolean = true
    ): Result<List<RadioStation>> =
        runCatching {
            api.advancedSearch(
                name = name,
                nameExact = nameExact,
                country = country,
                countryExact = countryExact,
                countryCode = countryCode,
                state = state,
                stateExact = stateExact,
                language = language,
                languageExact = languageExact,
                tag = tag,
                tagExact = tagExact,
                tagList = tagList,
                codec = codec,
                bitrateMin = bitrateMin,
                bitrateMax = bitrateMax,
                isHttps = isHttps,
                order = order,
                reverse = reverse,
                limit = limit,
                offset = offset,
                hideBroken = hideBroken
            ).map { it.toDomain() }
        }

    suspend fun getCountries(): Result<List<Country>> =
        runCatching {
            val cached = metadataDao.getCountries()
            if (cached.isNotEmpty()) {
                cached.map { it.toDomain() }
            } else {
                val remote = api.getCountries()
                    .map { it.toDomain() }
                    .filter { it.isoCode.isNotBlank() }
                
                metadataDao.insertCountries(remote.map { it.toEntity() })
                remote.sortedByDescending { it.stationCount }
            }
        }

    suspend fun getLanguages(filter: String? = null): Result<List<Language>> =
        runCatching {
            if (filter.isNullOrBlank()) {
                val cached = metadataDao.getLanguages()
                if (cached.isNotEmpty()) {
                    cached.map { it.toDomain() }
                } else {
                    val remote = api.getLanguages(order = "stationcount", reverse = true)
                        .map { it.toDomain() }
                    
                    metadataDao.insertLanguages(remote.map { it.toEntity() })
                    remote
                }
            } else {
                api.getLanguagesFiltered(filter = filter, order = "stationcount", reverse = true)
                    .map { it.toDomain() }
            }
        }

    suspend fun getCurrentCountryCode(): Result<String> =
        runCatching {
            val countryCode = context.resources.configuration.locales[0].country
            if (countryCode.isBlank()) {
                throw IllegalStateException("Country code not available in locale")
            }
            countryCode
        }

    /**
     * Should be called every time a user starts playing a station.
     * radio-browser.info uses this to rank station popularity.
     * Failures are silently ignored — this is a fire-and-forget call.
     */
    suspend fun registerClick(stationUuid: String) {
        runCatching { api.clickStation(stationUuid) }
    }
}