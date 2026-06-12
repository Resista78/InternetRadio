package com.armanmaurya.internetradio.di

import android.content.Context
import androidx.room.Room
import com.armanmaurya.internetradio.data.local.dao.FavoriteStationDao
import com.armanmaurya.internetradio.data.local.database.RadioDatabase
import com.armanmaurya.internetradio.data.remote.IpApi
import com.armanmaurya.internetradio.data.remote.RadioBrowserApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * radio-browser.info runs multiple community servers.
     * de1.api.radio-browser.info is the primary stable endpoint.
     * For production, consider resolving a random server via DNS SRV lookup:
     * https://docs.radio-browser.info/#server-selection
     */
    private const val BASE_URL = "https://de1.api.radio-browser.info/"
    private const val IP_API_BASE_URL = "http://ip-api.com/"
    private const val APP_USER_AGENT = "InternetRadio/1.0"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        return OkHttpClient.Builder()
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", APP_USER_AGENT)
                    .build()
                chain.proceed(request)
            }
            .addInterceptor(logging)
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @Named("RadioBrowserRetrofit")
    fun provideRadioBrowserRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    @Named("IpApiRetrofit")
    fun provideIpApiRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(IP_API_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideRadioBrowserApi(@Named("RadioBrowserRetrofit") retrofit: Retrofit): RadioBrowserApi =
        retrofit.create(RadioBrowserApi::class.java)

    @Provides
    @Singleton
    fun provideIpApi(@Named("IpApiRetrofit") retrofit: Retrofit): IpApi =
        retrofit.create(IpApi::class.java)

    @Provides
    @Singleton
    fun provideRadioDatabase(@ApplicationContext context: Context): RadioDatabase =
        Room.databaseBuilder(
            context,
            RadioDatabase::class.java,
            "radio_database"
        ).fallbackToDestructiveMigration(dropAllTables = true).build()

    @Provides
    @Singleton
    fun provideFavoriteStationDao(database: RadioDatabase): FavoriteStationDao =
        database.favoriteStationDao

    @Provides
    @Singleton
    fun provideMetadataDao(database: RadioDatabase): com.armanmaurya.internetradio.data.local.dao.MetadataDao =
        database.metadataDao

    @Provides
    @Singleton
    fun provideRecentStationDao(database: RadioDatabase): com.armanmaurya.internetradio.data.local.dao.RecentStationDao =
        database.recentStationDao
}