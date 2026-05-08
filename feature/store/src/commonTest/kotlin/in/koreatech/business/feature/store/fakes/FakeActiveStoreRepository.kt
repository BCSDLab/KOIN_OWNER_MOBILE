package `in`.koreatech.business.feature.store.fakes

import `in`.koreatech.business.domain.repository.ActiveStoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

internal class FakeActiveStoreRepository(initial: String? = null) : ActiveStoreRepository {
    private val state = MutableStateFlow(initial)
    override val activeStoreId: Flow<String?> get() = state
    override suspend fun setActiveStoreId(id: String?) {
        state.value = id
    }

    fun current(): String? = state.value
}
