package `in`.koreatech.business.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import `in`.koreatech.business.data.utils.createDataStore
import `in`.koreatech.business.data.utils.dataStoreFileName
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

actual val dataStoreModule = module {
    singleOf(::provideDataStore)
}

fun provideDataStore(context: Context): DataStore<Preferences> = createDataStore(
    producePath = { context.filesDir.resolve(dataStoreFileName).absolutePath }
)
