package me.data.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import me.domain.model.auth.NuboSigninResult
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

private val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_prefs")

/** 로그인 세션의 저장 형식을 서버 통신 코드와 분리한다. */
@Singleton
class UserSessionStore @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val userFlow: Flow<NuboSigninResult> = context.authDataStore.data.map(::toUser)

    suspend fun get(): NuboSigninResult = userFlow.first()

    suspend fun save(user: NuboSigninResult) {
        context.authDataStore.edit { prefs ->
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

    suspend fun clear() {
        context.authDataStore.edit { prefs -> prefs.clear() }
    }

    private fun toUser(prefs: Preferences): NuboSigninResult = NuboSigninResult(
        uid = prefs[UserPreferencesKeys.UID] ?: 0,
        name = prefs[UserPreferencesKeys.NAME] ?: "",
        profile = prefs[UserPreferencesKeys.PROFILE] ?: "",
        level = prefs[UserPreferencesKeys.LEVEL] ?: 0,
        signature = prefs[UserPreferencesKeys.SIGNATURE] ?: "",
        signup = parseDate(prefs[UserPreferencesKeys.SIGNUP]),
        signin = parseDate(prefs[UserPreferencesKeys.SIGNIN]),
        admin = prefs[UserPreferencesKeys.ADMIN] ?: false,
        blocked = prefs[UserPreferencesKeys.BLOCKED] ?: false,
        id = prefs[UserPreferencesKeys.ID] ?: "",
        point = prefs[UserPreferencesKeys.POINT] ?: 0,
        token = prefs[UserPreferencesKeys.TOKEN] ?: "",
        refresh = prefs[UserPreferencesKeys.REFRESH] ?: ""
    )

    private fun parseDate(value: String?): LocalDateTime =
        value?.let(LocalDateTime::parse) ?: LocalDateTime.now()
}
