package me.sensta.auth

import android.content.Context
import android.util.Base64
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import dagger.hilt.android.qualifiers.ApplicationContext
import me.sensta.R
import org.json.JSONObject
import javax.inject.Inject
import javax.inject.Singleton

/** Credential Manager 통신과 Google 응답 해석을 ViewModel 밖에서 담당한다. */
@Singleton
class GoogleCredentialClient @Inject constructor(
    @param:ApplicationContext private val applicationContext: Context
) {
    private val credentialManager = CredentialManager.create(applicationContext)

    suspend fun requestIdToken(activityContext: Context): GoogleCredentialResult {
        return try {
            val result = credentialManager.getCredential(
                request = createRequest(),
                context = activityContext
            )
            parseCredential(result.credential)
        } catch (error: NoCredentialException) {
            failure(
                "사용할 수 있는 Google 계정을 찾지 못했습니다. 기기 계정과 OAuth 설정을 확인해주세요.",
                error
            )
        } catch (error: GetCredentialException) {
            failure("Google 계정 인증을 완료하지 못했습니다. 잠시 후 다시 시도해주세요.", error)
        } catch (error: Exception) {
            failure("Google 로그인 응답을 처리하지 못했습니다.", error)
        }
    }

    suspend fun clearCredentialState() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
            Log.i(TAG, "Google credential 상태를 초기화했습니다.")
        } catch (error: ClearCredentialException) {
            Log.w(
                TAG,
                "Google credential 상태를 초기화하지 못했습니다. type=${error.type}",
                error
            )
        }
    }

    private fun createRequest(): GetCredentialRequest {
        // 사용자가 Google 버튼을 직접 눌렀으므로 button flow 전용 option을 사용한다.
        val option = GetSignInWithGoogleOption.Builder(
            applicationContext.getString(R.string.google_web_client_id)
        )
            .build()
        return GetCredentialRequest.Builder().addCredentialOption(option).build()
    }

    private fun parseCredential(credential: androidx.credentials.Credential): GoogleCredentialResult {
        if (credential !is CustomCredential) {
            return GoogleCredentialResult.Failure("Google 계정 인증 정보를 받지 못했습니다.")
        }
        if (credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
            return GoogleCredentialResult.Failure("Google 로그인 응답 형식을 확인할 수 없습니다.")
        }
        val idToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
        logIdTokenClaims(idToken)
        return GoogleCredentialResult.Success(idToken)
    }

    /** 토큰 원문이나 사용자 식별값 없이 서버 검증에 필요한 claim 형태만 기록한다. */
    private fun logIdTokenClaims(idToken: String) {
        runCatching {
            val payload = idToken.split('.').getOrNull(1)
                ?: error("ID token payload가 없습니다.")
            val json = JSONObject(
                String(Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING))
            )
            Log.i(
                TAG,
                "Google ID token claims: " +
                    "aud=${json.optString("aud")}, " +
                    "iss=${json.optString("iss")}, " +
                    "emailPresent=${json.has("email") && !json.isNull("email")}, " +
                    "emailVerified=${json.opt("email_verified")}, " +
                    "subPresent=${json.has("sub") && !json.isNull("sub")}"
            )
        }.onFailure { error ->
            Log.w(TAG, "Google ID token claim 진단에 실패했습니다.", error)
        }
    }

    private fun failure(message: String, error: Throwable): GoogleCredentialResult.Failure {
        val type = (error as? GetCredentialException)?.type ?: error.javaClass.name
        Log.e(TAG, "$message type=$type", error)
        return GoogleCredentialResult.Failure(message, error)
    }

    private companion object {
        const val TAG = "Sensta-GoogleAuth"
    }
}

sealed interface GoogleCredentialResult {
    data class Success(val idToken: String) : GoogleCredentialResult
    data class Failure(val message: String, val cause: Throwable? = null) : GoogleCredentialResult
}
