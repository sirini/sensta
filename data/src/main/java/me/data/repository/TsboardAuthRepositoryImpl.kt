package me.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.data.auth.UserPreferencesKeys
import me.data.remote.api.TsboardGoapi
import me.data.remote.dto.auth.toEntity
import me.data.util.toSHA256
import me.domain.model.auth.TsboardCheckEmail
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.emptyUser
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import java.time.LocalDateTime
import javax.inject.Inject

class TsboardAuthRepositoryImpl @Inject constructor(
    private val api: TsboardGoapi,
    @ApplicationContext private val context: Context
) : TsboardAuthRepository {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")
    private val userInfoFlow: Flow<TsboardSigninResult> = context.dataStore.data.map { prefs ->
        val uid = prefs[UserPreferencesKeys.UID] ?: 0
        val name = prefs[UserPreferencesKeys.NAME] ?: ""
        val profile = prefs[UserPreferencesKeys.PROFILE] ?: ""
        val level = prefs[UserPreferencesKeys.LEVEL] ?: 0
        val signature = prefs[UserPreferencesKeys.SIGNATURE] ?: ""
        val signup = prefs[UserPreferencesKeys.SIGNUP] ?: LocalDateTime.now().toString()
        val signin = prefs[UserPreferencesKeys.SIGNIN] ?: LocalDateTime.now().toString()
        val admin = prefs[UserPreferencesKeys.ADMIN] ?: false
        val blocked = prefs[UserPreferencesKeys.BLOCKED] ?: false
        val id = prefs[UserPreferencesKeys.ID] ?: ""
        val point = prefs[UserPreferencesKeys.POINT] ?: 0
        val token = prefs[UserPreferencesKeys.TOKEN] ?: ""
        val refresh = prefs[UserPreferencesKeys.REFRESH] ?: ""

        TsboardSigninResult(
            uid = uid,
            name = name,
            profile = profile,
            level = level,
            signature = signature,
            signup = LocalDateTime.parse(signup),
            signin = LocalDateTime.parse(signin),
            admin = admin,
            blocked = blocked,
            id = id,
            point = point,
            token = token,
            refresh = refresh
        )
    }

    // 이메일 주소를 쓸 수 있는지 확인하기
    override suspend fun checkEmail(email: String): TsboardResponse<TsboardCheckEmail> {
        return try {
            TsboardResponse.Success(api.checkID(email).toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 아이디와 비밀번호로 로그인하기
    override suspend fun signin(id: String, password: String): TsboardResponse<TsboardSignin> {
        return try {
            val hashedPassword = password.toSHA256()
            val response = api.signin(id, hashedPassword).toEntity()

            response.result?.also { saveUserInfo(it) }
            TsboardResponse.Success(response)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 사용자 로그인 후 정보를 Data Store에 보관하기
    override suspend fun saveUserInfo(user: TsboardSigninResult) {
        context.dataStore.edit { prefs ->
            prefs[UserPreferencesKeys.UID] = user.uid
            prefs[UserPreferencesKeys.NAME] = user.name
            prefs[UserPreferencesKeys.PROFILE] = user.profile
            prefs[UserPreferencesKeys.LEVEL] = user.level
            prefs[UserPreferencesKeys.SIGNATURE] = user.signature
            prefs[UserPreferencesKeys.SIGNUP] = user.signup.toString()
            prefs[UserPreferencesKeys.SIGNIN] = user.signin.toString()
            prefs[UserPreferencesKeys.ADMIN] = user.admin
            prefs[UserPreferencesKeys.BLOCKED] = user.blocked
            prefs[UserPreferencesKeys.ID] = user.id
            prefs[UserPreferencesKeys.POINT] = user.point
            prefs[UserPreferencesKeys.TOKEN] = user.token
            prefs[UserPreferencesKeys.REFRESH] = user.refresh
        }
    }

    // 사용자 로그인 후 정보를 가져오기
    override suspend fun getUserInfo(): TsboardSigninResult {
        return try {
            userInfoFlow.first()
        } catch (e: Exception) {
            emptyUser
        }
    }

    // Data Store에 보관했던 사용자 정보 지우기
    override suspend fun clearUserInfo() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }
}