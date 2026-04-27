package `in`.koreatech.business.feature.insertstore.di

import `in`.koreatech.business.feature.insertstore.InsertStoreViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val insertStoreModule = module {
    viewModelOf(::InsertStoreViewModel)
}
