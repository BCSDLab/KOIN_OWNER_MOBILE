package `in`.koreatech.business.data.repository

import `in`.koreatech.business.data.di.EncryptedDataStore
import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

class ActiveStoreRepositoryImpl(
    private val encryptedDataStore: EncryptedDataStore
) : ActiveStoreRepository {

    private val initMutex = Mutex()
    private var initialized = false
    private val _activeStoreId = MutableStateFlow<String?>(null)

    override val activeStoreId: Flow<String?> = flow {
        ensureInitialized()
        emitAll(_activeStoreId)
    }

    override suspend fun setActiveStoreId(id: String?) {
        ensureInitialized()
        val normalized = id?.trim()?.takeIf { it.isNotEmpty() }
        withContext(Dispatchers.Default) {
            if (normalized == null) {
                encryptedDataStore.deleteData(KEY)
            } else {
                encryptedDataStore.createData(KEY, normalized)
            }
        }
        _activeStoreId.value = normalized
    }

    private suspend fun ensureInitialized() = initMutex.withLock {
        if (initialized) return@withLock
        _activeStoreId.value = withContext(Dispatchers.Default) {
            encryptedDataStore.readData(KEY)?.trim()?.takeIf { it.isNotEmpty() }
        }
        initialized = true
    }

    private companion object {
        const val KEY = "lastActiveStoreId"
    }
}
