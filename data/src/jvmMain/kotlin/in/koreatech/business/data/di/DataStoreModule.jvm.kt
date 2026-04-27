package `in`.koreatech.business.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import `in`.koreatech.business.data.utils.createDataStore
import `in`.koreatech.business.data.utils.dataStoreFileName
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single
import org.koin.core.scope.Scope
import java.io.File
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private val appDataDir: File
    get() = File(System.getProperty("user.home"), ".koin_owner").also { it.mkdirs() }

private var dataStoreInstance: DataStore<Preferences>? = null

@Module
actual class DataStoreModule {
    @Single
    actual fun provideDataStore(scope: Scope): DataStore<Preferences> {
        return dataStoreInstance ?: createDataStore(
            producePath = { appDataDir.resolve(dataStoreFileName).absolutePath }
        ).also { dataStoreInstance = it }
    }
}

actual class EncryptedDataStore actual constructor(scope: Scope) {
    private val dataStore: DataStore<Preferences> = scope.get()

    private fun getOrCreateKey(): SecretKeySpec {
        val keyFile = appDataDir.resolve(".key")
        val keyBytes = if (keyFile.exists()) {
            keyFile.readBytes()
        } else {
            val key = KeyGenerator.getInstance("AES").also { it.init(128) }.generateKey()
            keyFile.writeBytes(key.encoded)
            key.encoded
        }
        return SecretKeySpec(keyBytes, "AES")
    }

    actual fun createData(key: String, value: String) {
        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, getOrCreateKey())
        val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val payload = cipher.iv + encrypted
        runBlocking {
            dataStore.edit { it[stringPreferencesKey(key)] = Base64.getEncoder().encodeToString(payload) }
        }
    }

    actual fun readData(key: String): String? = runBlocking {
        val encoded = dataStore.data.first()[stringPreferencesKey(key)] ?: return@runBlocking null
        try {
            val payload = Base64.getDecoder().decode(encoded)
            val iv = payload.copyOfRange(0, 12)
            val cipherText = payload.copyOfRange(12, payload.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, getOrCreateKey(), GCMParameterSpec(128, iv))
            String(cipher.doFinal(cipherText), Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }

    actual fun deleteData(key: String) {
        runBlocking {
            dataStore.edit { it.remove(stringPreferencesKey(key)) }
        }
    }
}
