@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package me.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import me.data.remote.api.PostApi
import me.data.repository.BoardRepositoryImpl
import me.domain.repository.BoardRepository
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    // Retrofit 객체 생성
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://sensta.me/goapi/")
            .addConverterFactory(
                Json.asConverterFactory("application/json".toMediaType())
            ).build()

    // 게시글 API 객체 생성
    @Provides
    @Singleton
    fun providePostApi(retrofit: Retrofit): PostApi = retrofit.create(PostApi::class.java)

    // 게시글 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun providePostRepository(api: PostApi): BoardRepository = BoardRepositoryImpl(api)
}