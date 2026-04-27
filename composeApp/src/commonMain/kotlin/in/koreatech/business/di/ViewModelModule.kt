package `in`.koreatech.business.di

import `in`.koreatech.business.AppViewModel
import `in`.koreatech.business.feature.findpassword.FindPasswordViewModel
import `in`.koreatech.business.feature.insertstore.InsertStoreViewModel
import `in`.koreatech.business.feature.settings.SettingsViewModel
import `in`.koreatech.business.feature.signin.SignInViewModel
import `in`.koreatech.business.feature.signup.SignupViewModel
import `in`.koreatech.business.feature.store.dashboard.StoreDashboardViewModel
import `in`.koreatech.business.feature.store.event.editor.WriteEventViewModel
import `in`.koreatech.business.feature.store.maintab.EventTabViewModel
import `in`.koreatech.business.feature.store.menu.categories.ManageCategoriesViewModel
import `in`.koreatech.business.feature.store.menu.editor.MenuEditorViewModel
import `in`.koreatech.business.feature.store.menu.manage.ManageMenusViewModel
import `in`.koreatech.business.feature.store.storeinfoedit.StoreInfoEditViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val ViewModelModule = module {
    viewModelOf(::AppViewModel)
    viewModelOf(::SignInViewModel)
    viewModelOf(::SignupViewModel)
    viewModelOf(::FindPasswordViewModel)
    viewModelOf(::StoreDashboardViewModel)
    viewModelOf(::ManageMenusViewModel)
    viewModelOf(::MenuEditorViewModel)
    viewModelOf(::WriteEventViewModel)
    viewModelOf(::StoreInfoEditViewModel)
    viewModelOf(::InsertStoreViewModel)
    viewModelOf(::ManageCategoriesViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::EventTabViewModel)
}
