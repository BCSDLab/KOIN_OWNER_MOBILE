package `in`.koreatech.business.feature.store.shared

data class ActiveStoreContext(
    val accountId: String,
    val activeStoreId: String?,
    val selectedFrom: StoreSelectionSource
)

enum class StoreSelectionSource {
    BOOTSTRAP,
    EXPLICIT_ROUTE_SCOPE,
    USER_SWITCH,
    RESTORE
}
