package `in`.koreatech.business.feature.store.menu.editor

sealed interface MenuEditorSideEffect {
    data object NavigateBack : MenuEditorSideEffect
}
