package me.sensta.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import me.sensta.R
import javax.inject.Inject

/** Credential Manager 통신과 Google 응답 해석을 ViewModel 밖에서 담당한다. */
class GoogleCredentialClient @Inject constructor() {
    suspend fun requestIdToken(context: Context): GoogleCredentialResult {
        return try {
            val result = CredentialManager.create(context).getCredential(
                request = createRequest(context),
                context = context
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

    private fun createRequest(context: Context): GetCredentialRequest {
        val option = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            // Firebase와 NUBO가 함께 검증하는 운영 Web OAuth client를 사용한다.
            .setServerClientId(context.getString(R.string.google_web_client_id))
            .setAutoSelectEnabled(false)
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
        return GoogleCredentialResult.Success(
            GoogleIdTokenCredential.createFrom(credential.data).idToken
        )
    }

    private fun failure(message: String, error: Throwable): GoogleCredentialResult.Failure {
        Log.e(TAG, message, error)
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
