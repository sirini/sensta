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
import me.data.remote.dto.common.toEntity
import me.data.util.toSHA256
import me.domain.model.auth.TsboardSignin
import me.domain.model.auth.TsboardSigninResult
import me.domain.model.auth.TsboardSignup
import me.domain.model.auth.TsboardUpdateAccessToken
import me.domain.model.auth.TsboardUpdateUserInfo
import me.domain.model.auth.TsboardUpdateUserInfoParam
import me.domain.model.auth.TsboardVerifyCodeParam
import me.domain.model.auth.emptyUser
import me.domain.model.common.TsboardResponseNothing
import me.domain.repository.TsboardAuthRepository
import me.domain.repository.TsboardResponse
import okhttp3.RequestBody.Companion.toRequestBody
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
    override suspend fun checkEmail(email: String): TsboardResponse<TsboardResponseNothing> {
        return try {
            TsboardResponse.Success(api.checkID(email).toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 닉네임을 쓸 수 있는지 확인하기
    override suspend fun checkName(name: String): TsboardResponse<TsboardResponseNothing> {
        return try {
            TsboardResponse.Success(api.checkName(name).toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // Data Store에 보관했던 사용자 정보 지우기
    override suspend fun clearUserInfo() {
        context.dataStore.edit { prefs -> prefs.clear() }
    }

    // 사용자 로그인 후 정보를 가져오기
    override suspend fun getUserInfo(): TsboardSigninResult {
        return try {
            userInfoFlow.first()
        } catch (e: Exception) {
            emptyUser
        }
    }

    // 아이디와 비밀번호로 로그인하기
    override suspend fun signIn(id: String, password: String): TsboardResponse<TsboardSignin> {
        return try {
            val hashedPassword = password.toSHA256()
            val response = api.signIn(id, hashedPassword).toEntity()

            response.result?.also { saveUserInfo(it) }
            TsboardResponse.Success(response)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 구글 계정으로 로그인하기
    override suspend fun signInWithGoogle(idToken: String): TsboardResponse<TsboardSignin> {
        return try {
            val response = api.signInWithGoogle(idToken).toEntity()
            response.result?.also { saveUserInfo(it) }
            TsboardResponse.Success(response)
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 회원가입하기
    override suspend fun signUp(
        id: String,
        password: String,
        name: String
    ): TsboardResponse<TsboardSignup> {
        return try {
            val response = api.signUp(id, password.toSHA256(), name).toEntity()
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

    // 리프레시 토큰으로 새 액세스 토큰 발급받기
    override suspend fun updateAccessToken(
        userUid: Int,
        refresh: String
    ): TsboardResponse<TsboardUpdateAccessToken> {
        return try {
            val response = api.updateAccessToken(userUid, refresh)
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 사용자의 정보 업데이트하기
    override suspend fun updateUserInfo(param: TsboardUpdateUserInfoParam): TsboardResponse<TsboardUpdateUserInfo> {
        return try {
            val response = api.updateUserInfo(
                authorization = param.authorization,
                name = param.name.toRequestBody(),
                signature = param.signature.toRequestBody(),
                password = if (param.password.length > 3) {
                    param.password.toSHA256()
                } else {
                    ""
                }.toRequestBody(),
                profile = param.profile
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }

    // 회원가입 시 인증코드 전송하고 결과 받기
    override suspend fun verifyCode(param: TsboardVerifyCodeParam): TsboardResponse<TsboardResponseNothing> {
        return try {
            val response = api.verifyCode(
                target = param.target,
                code = param.code,
                email = param.email,
                password = param.password.toSHA256(),
                name = param.name
            )
            TsboardResponse.Success(response.toEntity())
        } catch (e: Exception) {
            TsboardResponse.Error(e.localizedMessage ?: "An unexpected error occurred")
        }
    }
}