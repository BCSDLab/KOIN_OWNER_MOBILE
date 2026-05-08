package `in`.koreatech.business.data.source.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class TokenLocalDataSource(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun saveAccessToken(accessToken: String) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(ACCESS_TOKEN)] = accessToken
        }
    }

    suspend fun getAccessToken(): String = dataStore.data.first()[stringPreferencesKey(ACCESS_TOKEN)] ?: ""

    fun observeAccessToken(): Flow<String> = dataStore.data
        .map { it[stringPreferencesKey(ACCESS_TOKEN)] ?: "" }
        .distinctUntilChanged()

    suspend fun saveRefreshToken(refreshToken: String) {
        dataStore.edit { pref ->
            pref[stringPreferencesKey(REFRESH_TOKEN)] = refreshToken
        }
    }

    suspend fun getRefreshToken(): String = dataStore.data.first()[stringPreferencesKey(REFRESH_TOKEN)] ?: ""

    companion object {
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
    }
}
