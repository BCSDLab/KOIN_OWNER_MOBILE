package `in`.koreatech.business.feature.store.maintab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import `in`.koreatech.business.feature.store.shared.ActiveStoreContext
import `in`.koreatech.business.feature.store.shared.StoreSelectionSource
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun EventListScreen(
    storeId: String,
    onNavigateToEventEditor: (storeId: String) -> Unit,
    onNavigateToEditEvent: (storeId: String, eventId: String) -> Unit
) {
    val context = ActiveStoreContext(
        accountId = "",
        activeStoreId = storeId,
        selectedFrom = StoreSelectionSource.EXPLICIT_ROUTE_SCOPE
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            TabEventContent(
                activeStoreContext = context,
                onNavigateToEventEditor = onNavigateToEventEditor,
                onNavigateToEditEvent = onNavigateToEditEvent
            )
        }
    }
}
