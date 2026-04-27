@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.store.maintab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.feature.store.shared.ActiveStoreContext
import `in`.koreatech.business.platform.getCurrentDateString
import `in`.koreatech.business.ui.component.BusinessSnackbarHost
import `in`.koreatech.business.ui.component.GradientActionButton
import `in`.koreatech.business.ui.component.KoinAsyncImage
import `in`.koreatech.business.ui.component.KoinButton
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TabEventContent(
    activeStoreContext: ActiveStoreContext?,
    onNavigateToEventEditor: (storeId: String) -> Unit,
    onNavigateToEditEvent: (storeId: String, eventId: String) -> Unit = { _, _ -> },
    viewModel: EventTabViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val storeId = activeStoreContext?.activeStoreId
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(storeId) {
        if (!storeId.isNullOrBlank()) viewModel.load(storeId)
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            viewModel.clearError()
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && !storeId.isNullOrBlank()) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val today = remember { getCurrentDateString() }
    val filteredEvents = remember(uiState.events, uiState.filter, today) {
        uiState.events.filter { event ->
            when (uiState.filter) {
                EventFilter.All -> true
                EventFilter.Live -> event.status(today) == EventStatus.Live
                EventFilter.Planned -> event.status(today) == EventStatus.Planned
                EventFilter.Ended -> event.status(today) == EventStatus.Ended
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            if (uiState.isEditMode) {
                EditModeAppBar(
                    selectedCount = uiState.selectedEventIds.size,
                    onCancel = viewModel::toggleEditMode,
                    onDelete = viewModel::deleteSelected,
                    canDelete = uiState.selectedEventIds.isNotEmpty()
                )
            } else {
                NormalAppBar(
                    filter = uiState.filter,
                    onFilterChange = viewModel::setFilter,
                    onToggleEditMode = viewModel::toggleEditMode,
                    canEdit = uiState.events.isNotEmpty()
                )
            }

            when {
                storeId.isNullOrBlank() -> EmptyEventState("매장을 먼저 등록해주세요.", null, null, null)
                uiState.isLoading && uiState.events.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KoinTheme.colors.primary500)
                    }
                }
                filteredEvents.isEmpty() && uiState.events.isEmpty() -> {
                    EmptyEventState(
                        title = "진행 중인 이벤트가 없습니다.",
                        sub = "학생들에게 매장을 알려보세요.",
                        ctaLabel = "+ 이벤트 등록",
                        onCta = { onNavigateToEventEditor(storeId) }
                    )
                }
                filteredEvents.isEmpty() -> {
                    EmptyEventState(
                        title = "표시할 이벤트가 없습니다.",
                        sub = "다른 필터를 선택해 보세요.",
                        ctaLabel = null,
                        onCta = null
                    )
                }
                else -> {
                    EventList(
                        events = filteredEvents,
                        today = today,
                        isEditMode = uiState.isEditMode,
                        selectedIds = uiState.selectedEventIds,
                        expandedIds = uiState.expandedEventIds,
                        onToggleSelection = viewModel::toggleSelection,
                        onToggleExpand = viewModel::toggleExpanded,
                        onEditEvent = { eventId ->
                            if (!storeId.isNullOrBlank()) onNavigateToEditEvent(storeId, eventId)
                        }
                    )
                }
            }
        }

        // 등록 FAB (편집 모드 아닐 때만)
        if (!uiState.isEditMode && !storeId.isNullOrBlank() && uiState.events.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                GradientActionButton(
                    text = "+ 이벤트 등록",
                    onClick = { onNavigateToEventEditor(storeId) }
                )
            }
        }

        BusinessSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp)
        )
    }
}

@Composable
private fun NormalAppBar(
    filter: EventFilter,
    onFilterChange: (EventFilter) -> Unit,
    onToggleEditMode: () -> Unit,
    canEdit: Boolean
) {
    TopAppBar(
        title = {
            Text(
                text = "이벤트",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = KoinTheme.colors.neutral800
            )
        },
        actions = {
            FilterChips(filter = filter, onFilterChange = onFilterChange)
            if (canEdit) {
                IconButton(onClick = onToggleEditMode) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "편집",
                        tint = KoinTheme.colors.neutral800Variant,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = KoinTheme.colors.neutral0
        )
    )
}

@Composable
private fun EditModeAppBar(
    selectedCount: Int,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    canDelete: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KoinTheme.colors.neutral0)
            .height(56.dp)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "취소",
            modifier = Modifier
                .clickable(onClick = onCancel)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = KoinTheme.colors.neutral800Variant
        )
        Text(
            text = "${selectedCount}건 선택됨",
            modifier = Modifier.weight(1f),
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            color = KoinTheme.colors.neutral800,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Text(
            text = "삭제",
            modifier = Modifier
                .clickable(enabled = canDelete, onClick = onDelete)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = if (canDelete) KoinTheme.colors.danger600 else KoinTheme.colors.neutral500
        )
    }
}

@Composable
private fun FilterChips(filter: EventFilter, onFilterChange: (EventFilter) -> Unit) {
    val items = listOf(
        EventFilter.All to "전체",
        EventFilter.Live to "진행 중",
        EventFilter.Planned to "예정",
        EventFilter.Ended to "종료"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        items.forEach { (value, label) ->
            val selected = filter == value
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        if (selected) KoinTheme.colors.primary100 else Color.Transparent
                    )
                    .clickable { onFilterChange(value) }
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = label,
                    fontSize = 11.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral500
                )
            }
            Spacer(Modifier.width(2.dp))
        }
    }
}

@Composable
private fun EventList(
    events: List<StoreEvent>,
    today: String,
    isEditMode: Boolean,
    selectedIds: Set<Int>,
    expandedIds: Set<Int>,
    onToggleSelection: (Int) -> Unit,
    onToggleExpand: (Int) -> Unit,
    onEditEvent: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 96.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(events, key = { it.id }) { event ->
            EventCard(
                event = event,
                status = event.status(today),
                isExpanded = event.id in expandedIds,
                isEditMode = isEditMode,
                isSelected = event.id in selectedIds,
                onToggleExpand = { onToggleExpand(event.id) },
                onToggleSelect = { onToggleSelection(event.id) },
                onEdit = { onEditEvent(event.id.toString()) }
            )
        }
    }
}

@Composable
private fun EventCard(
    event: StoreEvent,
    status: EventStatus,
    isExpanded: Boolean,
    isEditMode: Boolean,
    isSelected: Boolean,
    onToggleExpand: () -> Unit,
    onToggleSelect: () -> Unit,
    onEdit: () -> Unit
) {
    val border = if (isEditMode && isSelected) {
        Modifier.border(
            width = 2.dp,
            color = KoinTheme.colors.primary500,
            shape = RoundedCornerShape(12.dp)
        )
    } else {
        Modifier.border(
            width = 1.dp,
            color = KoinTheme.colors.neutral300,
            shape = RoundedCornerShape(12.dp)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KoinTheme.colors.neutral0)
            .then(border)
            .clickable {
                if (isEditMode) onToggleSelect() else onToggleExpand()
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (isEditMode) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .size(22.dp)
                        .clip(RoundedCornerShape(11.dp))
                        .background(if (isSelected) KoinTheme.colors.primary500 else Color.Transparent)
                        .border(
                            width = 2.dp,
                            color = if (isSelected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral400,
                            shape = RoundedCornerShape(11.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(Modifier.width(12.dp))
            }
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(KoinTheme.colors.neutral200),
                contentAlignment = Alignment.Center
            ) {
                val firstUrl = event.thumbnailUrls.firstOrNull()
                if (firstUrl != null) {
                    KoinAsyncImage(
                        model = firstUrl,
                        contentDescription = event.title,
                        modifier = Modifier.size(64.dp),
                        cornerRadius = 10.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        tint = KoinTheme.colors.neutral400,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                StatusBadge(status = status)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = event.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KoinTheme.colors.neutral800
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${event.startDate.take(10)} ~ ${event.endDate.take(10)}",
                    fontSize = 11.sp,
                    color = KoinTheme.colors.neutral500
                )
            }
        }
        if (!isEditMode && isExpanded) {
            androidx.compose.material3.HorizontalDivider(
                color = KoinTheme.colors.neutral200,
                thickness = 1.dp
            )
            if (event.content.isNotEmpty()) {
                Text(
                    text = event.content,
                    fontSize = 12.sp,
                    color = KoinTheme.colors.neutral800Variant,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "수정",
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable(onClick = onEdit)
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KoinTheme.colors.primary500
                )
            }
        }
    }
}

@Composable
private fun StatusBadge(status: EventStatus) {
    val (label, bg, fg) = when (status) {
        EventStatus.Live -> Triple("진행 중", KoinTheme.colors.success100, KoinTheme.colors.success700)
        EventStatus.Planned -> Triple("예정", KoinTheme.colors.primary100, KoinTheme.colors.primary500)
        EventStatus.Ended -> Triple("종료", KoinTheme.colors.neutral200, KoinTheme.colors.neutral500)
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = fg
        )
    }
}

@Composable
private fun EmptyEventState(
    title: String,
    sub: String?,
    ctaLabel: String?,
    onCta: (() -> Unit)?
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(28.dp))
                .background(KoinTheme.colors.neutral200),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.LocalOffer,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.height(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = KoinTheme.colors.neutral800
        )
        if (sub != null) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = sub,
                fontSize = 13.sp,
                color = KoinTheme.colors.neutral500
            )
        }
        if (ctaLabel != null && onCta != null) {
            Spacer(Modifier.height(20.dp))
            KoinButton(text = ctaLabel, onClick = onCta)
        }
    }
}

private enum class EventStatus { Live, Planned, Ended }

private fun StoreEvent.status(today: String): EventStatus {
    val s = startDate.take(10)
    val e = endDate.take(10)
    return when {
        today < s -> EventStatus.Planned
        today > e -> EventStatus.Ended
        else -> EventStatus.Live
    }
}
