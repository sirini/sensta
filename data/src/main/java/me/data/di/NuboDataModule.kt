@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package me.data.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import me.data.diagnostics.NuboNetworkDiagnosticsInterceptor
import me.data.auth.UserSessionStore
import me.data.remote.api.NuboAuthApi
import me.data.remote.api.NuboBoardApi
import me.data.remote.api.NuboNotificationApi
import me.data.remote.api.NuboUserApi
import me.data.repository.NuboAuthRepositoryImpl
import me.data.repository.NuboBoardRepositoryImpl
import me.data.repository.NuboNotificationRepositoryImpl
import me.data.repository.NuboUserChatRepositoryImpl
import me.domain.repository.NuboAuthRepository
import me.domain.repository.NuboBoardRepository
import me.domain.repository.NuboNotificationRepository
import me.domain.repository.NuboUserChatRepository
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NuboDataModule {
    private val apiJson = Json {
        // 서버가 새 필드를 추가해도 구버전 앱의 기존 기능은 계속 동작해야 한다.
        ignoreUnknownKeys = true
    }

    // 민감한 값은 제외하고 요청 성공 여부와 소요 시간을 Logcat에 남긴다.
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(NuboNetworkDiagnosticsInterceptor())
        .build()

    // Retrofit 객체 생성
    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://sensta.me/goapi/")
            .client(client)
            .addConverterFactory(
                apiJson.asConverterFactory("application/json".toMediaType())
            ).build()

    // 기능별 API를 분리해 각 저장소가 필요한 서버 기능에만 의존하게 한다.
    @Provides
    @Singleton
    fun provideNuboAuthApi(retrofit: Retrofit): NuboAuthApi =
        retrofit.create(NuboAuthApi::class.java)

    @Provides
    @Singleton
    fun provideNuboBoardApi(retrofit: Retrofit): NuboBoardApi =
        retrofit.create(NuboBoardApi::class.java)

    @Provides
    @Singleton
    fun provideNuboNotificationApi(retrofit: Retrofit): NuboNotificationApi =
        retrofit.create(NuboNotificationApi::class.java)

    @Provides
    @Singleton
    fun provideNuboUserApi(retrofit: Retrofit): NuboUserApi =
        retrofit.create(NuboUserApi::class.java)

    // 게시글 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideNuboRepository(api: NuboBoardApi): NuboBoardRepository =
        NuboBoardRepositoryImpl(api)

    // 인증용 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideNuboAuthRepository(
        api: NuboAuthApi,
        sessionStore: UserSessionStore
    ): NuboAuthRepository = NuboAuthRepositoryImpl(api, sessionStore)

    // 알림용 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideNuboNotificationRepository(
        api: NuboNotificationApi
    ): NuboNotificationRepository = NuboNotificationRepositoryImpl(api)

    // 다른 사용자와의 상호작용을 위한 리포지토리 구현체 생성
    @Provides
    @Singleton
    fun provideNuboUserRepository(
        api: NuboUserApi
    ): NuboUserChatRepository = NuboUserChatRepositoryImpl(api)
}
