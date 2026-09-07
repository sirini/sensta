package me.data.repository

import me.data.auth.UserSessionStore
import me.data.remote.api.NuboAuthApi
import me.data.remote.dto.auth.toEntity
import me.data.remote.dto.auth.MobileRefreshRequestDto
import me.data.remote.dto.auth.DeleteAccountRequestDto
import me.data.remote.dto.auth.PasswordResetRequestDto
import me.data.remote.dto.common.toEntity
import me.data.remote.dto.common.AchievementAcknowledgeRequestDto
import me.domain.model.auth.NuboSignin
import me.domain.model.auth.NuboSigninResult
import me.domain.model.auth.NuboSignup
import me.domain.model.auth.NuboSignupStatus
import me.domain.model.auth.NuboUpdateAccessToken
import me.domain.model.auth.NuboUpdateUserInfo
import me.domain.model.auth.NuboUpdateUserInfoParam
import me.domain.model.auth.NuboVerifyCodeParam
import me.domain.model.auth.emptyUser
import me.domain.model.common.NuboResponseNothing
import me.domain.model.common.NuboBadge
import me.domain.repository.NuboAuthRepository
import me.domain.repository.NuboResponse
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject

class NuboAuthRepositoryImpl @Inject constructor(
    private val api: NuboAuthApi,
    private val sessionStore: UserSessionStore
) : NuboAuthRepository {

    override suspend fun getSignupStatus(): NuboResponse<NuboSignupStatus> {
        return try {
            val response = api.getSignupStatus()
            val result = response.result
            if (!response.success || result == null) {
                NuboResponse.Error(response.error.ifBlank { "가입 정책을 불러오지 못했습니다" })
            } else {
                NuboResponse.Success(result.toEntity())
            }
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "가입 정책을 불러오지 못했습니다", cause = e)
        }
    }

    override suspend fun requestPasswordReset(email: String): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(api.requestPasswordReset(PasswordResetRequestDto(email)).toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "비밀번호 재설정 메일을 요청하지 못했습니다", cause = e)
        }
    }

    override suspend fun logout(token: String): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(api.logout("Bearer $token").toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "서버 로그아웃에 실패했습니다", cause = e)
        }
    }

    override suspend fun getUnannouncedAchievements(token: String): NuboResponse<List<NuboBadge>> {
        return try {
            val response = api.getUnannouncedAchievements("Bearer $token")
            if (response.success) {
                NuboResponse.Success(response.result.map { it.toEntity() })
            } else {
                NuboResponse.Error(response.error.ifBlank { "업적을 불러오지 못했습니다" })
            }
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "업적을 불러오지 못했습니다", cause = e)
        }
    }

    override suspend fun acknowledgeAchievements(
        token: String,
        keys: List<String>
    ): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(
                api.acknowledgeAchievements(
                    authorization = "Bearer $token",
                    request = AchievementAcknowledgeRequestDto(keys)
                ).toEntity()
            )
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "업적 확인을 저장하지 못했습니다", cause = e)
        }
    }

    // 이메일 주소를 쓸 수 있는지 확인하기
    override suspend fun checkEmail(email: String): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(api.checkID(email).toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 닉네임을 쓸 수 있는지 확인하기
    override suspend fun checkName(name: String): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(api.checkName(name).toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // Data Store에 보관했던 사용자 정보 지우기
    override suspend fun clearUserInfo() {
        sessionStore.clear()
    }

    // 서버 계정과 연관 데이터를 영구 삭제하기
    override suspend fun deleteAccount(token: String): NuboResponse<NuboResponseNothing> {
        return try {
            NuboResponse.Success(
                api.deleteAccount(
                    authorization = "Bearer $token",
                    request = DeleteAccountRequestDto(confirmation = "DELETE")
                ).toEntity()
            )
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "계정 삭제에 실패했습니다", cause = e)
        }
    }

    // 사용자 로그인 후 정보를 가져오기
    override suspend fun getUserInfo(): NuboSigninResult {
        return try {
            sessionStore.get()
        } catch (e: Exception) {
            emptyUser
        }
    }

    // 아이디와 비밀번호로 로그인하기
    override suspend fun signIn(id: String, password: String): NuboResponse<NuboSignin> {
        return try {
            val response = api.signIn(id, password).toEntity()

            response.result?.also { saveUserInfo(it) }
            NuboResponse.Success(response)
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 구글 계정으로 로그인하기
    override suspend fun signInWithGoogle(idToken: String): NuboResponse<NuboSignin> {
        return try {
            val response = api.signInWithGoogle(idToken).toEntity()
            response.result?.also { saveUserInfo(it) }
            NuboResponse.Success(response)
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 회원가입하기
    override suspend fun signUp(
        id: String,
        password: String,
        name: String,
        invite: String
    ): NuboResponse<NuboSignup> {
        return try {
            val response = api.signUp(id, password, name, invite).toEntity()
            NuboResponse.Success(response)
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 사용자 로그인 후 정보를 Data Store에 보관하기
    override suspend fun saveUserInfo(user: NuboSigninResult) {
        sessionStore.save(user)
    }

    // 리프레시 토큰으로 새 액세스 토큰 발급받기
    override suspend fun updateAccessToken(refresh: String): NuboResponse<NuboUpdateAccessToken> {
        return try {
            val response = api.updateAccessToken(MobileRefreshRequestDto(refresh))
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 사용자의 정보 업데이트하기
    override suspend fun updateUserInfo(param: NuboUpdateUserInfoParam): NuboResponse<NuboUpdateUserInfo> {
        return try {
            val response = api.updateUserInfo(
                authorization = "Bearer ${param.authorization}",
                name = param.name.toRequestBody(),
                signature = param.signature.toRequestBody(),
                password = if (param.password.length > 3) {
                    param.password
                } else {
                    ""
                }.toRequestBody(),
                profile = param.profile
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }

    // 회원가입 시 인증코드 전송하고 결과 받기
    override suspend fun verifyCode(param: NuboVerifyCodeParam): NuboResponse<NuboResponseNothing> {
        return try {
            val response = api.verifyCode(
                target = param.target,
                code = param.code,
                email = param.email,
                password = param.password,
                name = param.name
            )
            NuboResponse.Success(response.toEntity())
        } catch (e: Exception) {
            NuboResponse.Error(message = e.localizedMessage ?: "An unexpected error occurred", cause = e)
        }
    }
}
