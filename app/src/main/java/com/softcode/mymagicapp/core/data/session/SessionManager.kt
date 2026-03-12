package com.softcode.mymagicapp.core.data.session

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val USERNAME_KEY = stringPreferencesKey("username")
        private val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    // Expone un Flow con los datos de sesión.
    // Si el token es null, entendemos que no hay sesión activa.
    val sessionData: Flow<UserSession?> = context.dataStore.data.map { prefs ->
        val username = prefs[USERNAME_KEY]
        val token = prefs[AUTH_TOKEN_KEY]

        if (username != null && token != null) {
            UserSession(username, token)
        } else {
            null
        }
    }

    suspend fun saveSession(username: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[USERNAME_KEY] = username
            prefs[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs.remove(USERNAME_KEY)
            prefs.remove(AUTH_TOKEN_KEY)
        }
    }
}