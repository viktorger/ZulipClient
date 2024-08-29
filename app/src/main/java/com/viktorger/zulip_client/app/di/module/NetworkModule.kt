package com.viktorger.zulip_client.app.di.module

import com.viktorger.zulip_client.app.BuildConfig
import com.viktorger.zulip_client.app.core.network.AuthHeaderInterceptor
import com.viktorger.zulip_client.app.core.network.ZulipApi
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object NetworkModule {
    @Singleton
    @Provides
    fun provideZulipApi(
        client: OkHttpClient,
        jsonSerializer: Json
    ): ZulipApi = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL)
        .addConverterFactory(
            jsonSerializer.asConverterFactory(
                "application/json; charset=UTF8".toMediaType()
            )
        )
        .client(client)
        .build()
        .create()

    @Provides
    fun provideAuthClient(): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(AuthHeaderInterceptor())
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .build()

    @Provides
    fun provideJsonSerializer(): Json = Json {
        ignoreUnknownKeys = true
    }

}