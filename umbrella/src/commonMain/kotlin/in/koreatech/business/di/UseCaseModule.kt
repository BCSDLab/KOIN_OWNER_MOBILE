package `in`.koreatech.business.di

import `in`.koreatech.business.domain.usecase.auth.ChangePasswordBySmsUseCase
import `in`.koreatech.business.domain.usecase.auth.CheckPhoneExistsUseCase
import `in`.koreatech.business.domain.usecase.auth.DeleteAccountUseCase
import `in`.koreatech.business.domain.usecase.auth.RegisterUseCase
import `in`.koreatech.business.domain.usecase.auth.SendFindPasswordSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SendSignupSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.SignInUseCase
import `in`.koreatech.business.domain.usecase.auth.SignOutUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifyFindPasswordSmsUseCase
import `in`.koreatech.business.domain.usecase.auth.VerifySignupSmsUseCase
import `in`.koreatech.business.domain.usecase.owner.GetOwnerProfileUseCase
import `in`.koreatech.business.domain.usecase.owner.GetRequiredVersionUseCase
import `in`.koreatech.business.domain.usecase.owner.GetShopListUseCase
import `in`.koreatech.business.domain.usecase.owner.SearchShopsUseCase
import `in`.koreatech.business.domain.usecase.owner.UploadFileUseCase
import `in`.koreatech.business.domain.usecase.preferences.ObserveThemeModeUseCase
import `in`.koreatech.business.domain.usecase.preferences.SetThemeModeUseCase
import `in`.koreatech.business.domain.usecase.store.CreateMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteEventUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.DeleteMenuUseCase
import `in`.koreatech.business.domain.usecase.store.GetMenuCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreCategoriesUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreDetailUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreEventsUseCase
import `in`.koreatech.business.domain.usecase.store.GetStoreMenusUseCase
import `in`.koreatech.business.domain.usecase.store.ObserveActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterEventUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterMenuUseCase
import `in`.koreatech.business.domain.usecase.store.RegisterStoreUseCase
import `in`.koreatech.business.domain.usecase.store.RenameMenuCategoryUseCase
import `in`.koreatech.business.domain.usecase.store.SetActiveStoreIdUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateEventUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateMenuUseCase
import `in`.koreatech.business.domain.usecase.store.UpdateStoreInfoUseCase
import `in`.koreatech.business.domain.usecase.token.ClearTokensUseCase
import `in`.koreatech.business.domain.usecase.token.GetAccessTokenUseCase
import org.koin.dsl.module

val useCaseModule = module {
    single { SignInUseCase(get()) }
    single { SignOutUseCase(get()) }
    single { DeleteAccountUseCase(get()) }
    single { CheckPhoneExistsUseCase(get()) }
    single { SendSignupSmsUseCase(get()) }
    single { VerifySignupSmsUseCase(get()) }
    single { RegisterUseCase(get()) }
    single { SendFindPasswordSmsUseCase(get()) }
    single { VerifyFindPasswordSmsUseCase(get()) }
    single { ChangePasswordBySmsUseCase(get()) }
    single { GetShopListUseCase(get()) }
    single { GetOwnerProfileUseCase(get()) }
    single { GetRequiredVersionUseCase(get()) }
    single { UploadFileUseCase(get()) }
    single { SearchShopsUseCase(get()) }
    single { GetAccessTokenUseCase(get()) }
    single { ClearTokensUseCase(get()) }
    single { ObserveThemeModeUseCase(get()) }
    single { SetThemeModeUseCase(get()) }
    single { ObserveActiveStoreIdUseCase(get()) }
    single { SetActiveStoreIdUseCase(get()) }
    single { GetStoreDetailUseCase(get()) }
    single { GetStoreMenusUseCase(get()) }
    single { GetStoreEventsUseCase(get()) }
    single { GetMenuCategoriesUseCase(get()) }
    single { GetStoreCategoriesUseCase(get()) }
    single { RegisterMenuUseCase(get()) }
    single { UpdateMenuUseCase(get()) }
    single { DeleteMenuUseCase(get()) }
    single { RegisterEventUseCase(get()) }
    single { UpdateEventUseCase(get()) }
    single { DeleteEventUseCase(get()) }
    single { UpdateStoreInfoUseCase(get()) }
    single { CreateMenuCategoryUseCase(get()) }
    single { RenameMenuCategoryUseCase(get()) }
    single { DeleteMenuCategoryUseCase(get()) }
    single { RegisterStoreUseCase(get()) }
}
