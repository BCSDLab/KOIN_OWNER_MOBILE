package `in`.koreatech.business.feature.store.di

import `in`.koreatech.business.feature.store.dashboard.StoreDashboardViewModel
import `in`.koreatech.business.feature.store.event.editor.WriteEventViewModel
import `in`.koreatech.business.feature.store.maintab.EventTabViewModel
import `in`.koreatech.business.feature.store.menu.categories.ManageCategoriesViewModel
import `in`.koreatech.business.feature.store.menu.editor.MenuEditorViewModel
import `in`.koreatech.business.feature.store.menu.manage.ManageMenusViewModel
import `in`.koreatech.business.feature.store.storeinfoedit.StoreInfoEditViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val storeModule = module {
    viewModelOf(::StoreDashboardViewModel)
    viewModelOf(::ManageMenusViewModel)
    viewModelOf(::MenuEditorViewModel)
    viewModelOf(::WriteEventViewModel)
    viewModelOf(::EventTabViewModel)
    viewModelOf(::ManageCategoriesViewModel)
    viewModelOf(::StoreInfoEditViewModel)
}
