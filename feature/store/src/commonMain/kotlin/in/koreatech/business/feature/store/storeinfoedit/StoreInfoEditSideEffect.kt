package `in`.koreatech.business.feature.store.storeinfoedit

sealed interface StoreInfoEditSideEffect {
    data object NavigateBack : StoreInfoEditSideEffect
}
