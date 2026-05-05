package `in`.koreatech.business.data.di

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import `in`.koreatech.business.data.utils.createDataStore
import `in`.koreatech.business.data.utils.dataStoreFileName
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.scope.Scope

// Koin 컨테이너가 재생성되어도 같은 파일에 DataStore가 중복 생성되지 않도록 프로세스 수준 싱글톤 유지
private var dataStoreInstance: DataStore<Preferences>? = null

actual class DataStoreModule {
    actual fun provideDataStore(scope: Scope): DataStore<Preferences> {
        val context: Context = scope.get()
        return dataStoreInstance ?: createDataStore(
            producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
        ).also { dataStoreInstance = it }
    }
}

private fun generateKey() {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    if (keyStore.containsAlias(KEY_ALIAS)) return

    val keyGenerator = KeyGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_AES,
        "AndroidKeyStore"
    )
    val keyGenParameterSpec = KeyGenParameterSpec.Builder(
        KEY_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setRandomizedEncryptionRequired(true)
        .build()

    keyGenerator.init(keyGenParameterSpec)
    keyGenerator.generateKey()
}

private fun encryptData(value: String): String {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey) // IV는 KeyStore가 랜덤 생성

    val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

    val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.NO_WRAP)
    val ivBase64 = Base64.encodeToString(cipher.iv, Base64.NO_WRAP)

    return "$ivBase64:$encryptedBase64"
}

private fun decryptData(value: String): String {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

    val delimIndex = value.indexOf(':')
    if (delimIndex < 0) return ""

    val iv = Base64.decode(value.substring(0, delimIndex), Base64.NO_WRAP)
    val encryptedBytes = Base64.decode(value.substring(delimIndex + 1), Base64.NO_WRAP)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

    val decryptedBytes = cipher.doFinal(encryptedBytes)
    return String(decryptedBytes)
}

private fun String?.decrypt() = if (this != null) decryptData(this) else ""

actual class EncryptedDataStore actual constructor(scope: Scope) {
    val dataStore: DataStore<Preferences> = scope.get()

    actual fun createData(key: String, value: String) {
        generateKey()
        runBlocking {
            dataStore.edit {
                it[stringPreferencesKey(key)] = encryptData(value)
            }
        }
    }

    actual fun readData(key: String): String? = runBlocking { dataStore.data.first()[stringPreferencesKey(key)].decrypt() }

    actual fun deleteData(key: String) {
        runBlocking {
            dataStore.edit {
                it.remove(stringPreferencesKey(key))
            }
        }
    }
}

const val KEY_ALIAS = "KOIN_OWNER_KEY"
