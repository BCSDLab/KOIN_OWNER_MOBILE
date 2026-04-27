package `in`.koreatech.business.feature.auth.di

import `in`.koreatech.business.feature.findpassword.FindPasswordViewModel
import `in`.koreatech.business.feature.signin.SignInViewModel
import `in`.koreatech.business.feature.signup.SignupViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val authModule = module {
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::FindPasswordViewModel)
}
