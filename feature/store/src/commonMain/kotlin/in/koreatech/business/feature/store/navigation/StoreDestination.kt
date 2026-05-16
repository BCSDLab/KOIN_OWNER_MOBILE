package `in`.koreatech.business.feature.store.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalOffer
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Store
import androidx.compose.ui.graphics.vector.ImageVector
import koreatech.business.designsystem.resources.*
import org.jetbrains.compose.resources.StringResource

/**
 * store 최상위 네비게이션 목적지 단일 모델.
 *
 * - [TopTab]: NavigationSuiteScaffold 탭에 노출되는 최상위 탭 4개.
 *   각 항목은 [labelRes], [iconFilled], [iconOutlined] 를 보유한다.
 * - [SubSection]: 탭에는 없지만 사이드바에 표시되는 보조 항목
 *   (Categories, StoreInfo, Theme, Terms, Privacy, OssLicenses).
 * - [None]: "선택 없음" 센티넬. StoreSubScreenLayout 의 기본값으로 사용된다.
 */
sealed interface StoreDestination {

    /** NavigationSuiteScaffold 탭에 노출되는 항목 */
    enum class TopTab(
        val labelRes: StringResource,
        val iconFilled: ImageVector,
        val iconOutlined: ImageVector
    ) : StoreDestination {
        Dashboard(Res.string.sidebar_dashboard, Icons.Filled.Home, Icons.Outlined.Home),
        Menu(Res.string.tab_menu, Icons.Filled.Store, Icons.Outlined.Store),
        Events(Res.string.tab_events, Icons.Filled.LocalOffer, Icons.Outlined.LocalOffer),
        More(Res.string.tab_more, Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz)
    }

    /** 사이드바 전용 보조 항목 (탭 바에는 없음) */
    enum class SubSection : StoreDestination {
        Categories,
        StoreInfo,
        Theme,
        Terms,
        Privacy,
        OssLicenses
    }

    /** 선택 없음 센티넬 */
    data object None : StoreDestination
}
