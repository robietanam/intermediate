package com.bangkit.navigationsubmission.data.preferences
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.androidintermedieatesubmission.helper.AuthData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "auth")

class AuthPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val TOKEN_KEY = stringPreferencesKey("token_key")
    private val USER_ID = stringPreferencesKey("user_ID")
    private val NAME = stringPreferencesKey("name")


    fun getCredential(): Flow<AuthData> {
        return dataStore.data.map { preferences ->
            AuthData(token = preferences[TOKEN_KEY], nama = preferences[NAME], userId = preferences[USER_ID])
        }
    }


    suspend fun saveCredential(data: AuthData) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = data.token ?: ""
            preferences[USER_ID] = data.userId ?: ""
            preferences[NAME] = data.nama ?: ""
        }
    }


    companion object {
        @Volatile
        private var INSTANCE: AuthPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): AuthPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = AuthPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }
}

class SettingPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    private val SET_LANGUANGE = stringPreferencesKey("languange")


    fun getLanguangeSetting(): Flow<String> {
        return dataStore.data.map { preferences ->
           preferences[SET_LANGUANGE] ?: "ID"
        }
    }

    suspend fun saveLanguangeSetting(lang: String) {
        dataStore.edit { preferences ->
            preferences[SET_LANGUANGE] = lang
        }
    }



    companion object {
        @Volatile
        private var INSTANCE: SettingPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): SettingPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }

    }
}