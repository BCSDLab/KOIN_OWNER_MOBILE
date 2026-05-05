package `in`.koreatech.business.domain.repository

import kotlinx.coroutines.flow.Flow

interface ActiveStoreRepository {
    val activeStoreId: Flow<String?>

    suspend fun setActiveStoreId(id: String?)
}
