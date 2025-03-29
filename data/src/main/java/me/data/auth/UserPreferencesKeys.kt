package me.data.auth

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object UserPreferencesKeys {
    val UID = intPreferencesKey("uid")
    val NAME = stringPreferencesKey("name")
    val PROFILE = stringPreferencesKey("profile")
    val LEVEL = intPreferencesKey("level")
    val SIGNATURE = stringPreferencesKey("signature")
    val SIGNUP = stringPreferencesKey("signup")
    val SIGNIN = stringPreferencesKey("signin")
    val ADMIN = booleanPreferencesKey("admin")
    val BLOCKED = booleanPreferencesKey("blocked")
    val ID = stringPreferencesKey("id")
    val POINT = intPreferencesKey("point")
    val TOKEN = stringPreferencesKey("token")
    val REFRESH = stringPreferencesKey("refresh")
}