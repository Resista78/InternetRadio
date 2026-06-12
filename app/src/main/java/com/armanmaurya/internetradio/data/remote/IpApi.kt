package com.armanmaurya.internetradio.data.remote

import com.armanmaurya.internetradio.data.remote.dto.IpApiResponse
import retrofit2.http.GET

interface IpApi {
    @GET("json")
    suspend fun getCurrentLocation(): IpApiResponse
}
