package `in`.koreatech.business.feature.signin

sealed interface SignInSideEffect {
    data object NavigateToStoreMain : SignInSideEffect
    data object NavigateToStoreRegister : SignInSideEffect
}
