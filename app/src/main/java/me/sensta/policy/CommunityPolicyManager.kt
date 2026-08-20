package me.sensta.policy

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommunityPolicyManager @Inject constructor(
    @param:ApplicationContext private val context: Context
) {
    private val preferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    fun isAccepted(): Boolean = preferences.getString(KEY_ACCEPTED_VERSION, "") == POLICY_VERSION

    fun accept() {
        preferences.edit().putString(KEY_ACCEPTED_VERSION, POLICY_VERSION).apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "community_policy"
        const val KEY_ACCEPTED_VERSION = "accepted_version"
        const val POLICY_VERSION = "2026-08-21"
    }
}
