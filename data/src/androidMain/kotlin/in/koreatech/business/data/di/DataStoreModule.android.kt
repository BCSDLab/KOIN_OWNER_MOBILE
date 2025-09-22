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
import javax.crypto.spec.IvParameterSpec
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope

@Module
actual class DataStoreModule {
    @Single
    actual fun provideDataStore(scope: Scope): DataStorePlatformModule = DataStoreAndroidModule(scope)
}

class DataStoreAndroidModule(scope: Scope) : DataStorePlatformModule {
    val context: Context = scope.get()
    override fun provideDataStore(): DataStore<Preferences> = createDataStore(
        producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
    )
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

    val iv = ByteArray(16)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, IvParameterSpec(iv))

    val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))

    val encryptedBase64 = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
    val ivBase64 = Base64.encodeToString(iv, Base64.DEFAULT)

    return "$ivBase64:$encryptedBase64"
}

private fun decryptData(value: String): String {
    val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
    val secretKey = keyStore.getKey(KEY_ALIAS, null) as SecretKey

    val encryptedBytes = Base64.decode(value.substring(16..value.length), Base64.DEFAULT)
    val iv = Base64.decode(value.substring(0 until 15), Base64.DEFAULT)

    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    val spec = GCMParameterSpec(128, iv)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

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
