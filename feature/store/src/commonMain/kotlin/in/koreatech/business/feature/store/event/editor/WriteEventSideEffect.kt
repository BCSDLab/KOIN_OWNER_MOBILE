package `in`.koreatech.business.feature.store.event.editor

sealed interface WriteEventSideEffect {
    data object NavigateBack : WriteEventSideEffect
}
