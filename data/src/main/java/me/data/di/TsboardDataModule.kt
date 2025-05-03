@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package me.data.di

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import me.data.remote.api.TsboardGoapi
import me.data.repository.TsboardAuthRepositoryImpl
import me.data.repository.TsboardBoardRepositoryImpl
import me.data.repository.TsboardNotificationRepositoryImpl
import me.data.repository.TsboardUserChatRepositoryImpl
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardBoardRepository
import me.domain.repository.TsboardNotificationRepository
import me.domain.repository.TsboardUserChatRepository
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TsboardDataModule {

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
    fun provideTsboardGoapi(retrofit: Retrofit): TsboardGoapi =
        retrofit.create(TsboardGoapi::class.java)

    // 게시글 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideTsboardRepository(api: TsboardGoapi): TsboardBoardRepository =
        TsboardBoardRepositoryImpl(api)

    // 인증용 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideTsboardAuthRepository(
        api: TsboardGoapi,
        @ApplicationContext context: Context
    ): TsboardAuthRepository = TsboardAuthRepositoryImpl(api, context)

    // 알림용 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideTsboardNotificationRepository(
        api: TsboardGoapi
    ): TsboardNotificationRepository = TsboardNotificationRepositoryImpl(api)

    // 다른 사용자와의 상호작용을 위한 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideTsboardUserRepository(
        api: TsboardGoapi
    ): TsboardUserChatRepository = TsboardUserChatRepositoryImpl(api)
}