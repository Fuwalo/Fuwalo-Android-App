package com.example.fuwalo_test.di

import com.example.fuwalo_test.data.MidiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
@InstallIn(ViewModelComponent::class)
object MidiServiceModule {


    private const val BASE_URL = "http://15.223.50.164/"

    @Provides
    fun provideLoggingInterceptor() = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    @Provides
    fun provideOkhttp(
        loggingInterceptor: HttpLoggingInterceptor
    ):OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .callTimeout(45L , TimeUnit.SECONDS)
            .readTimeout(45L, TimeUnit.SECONDS)
            .connectTimeout(45L, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    fun provideMidiService(
        okHttpClient: OkHttpClient
    ): MidiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MidiService::class.java)
    }

}