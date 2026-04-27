@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.store.maintab

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.StoreEvent
import `in`.koreatech.business.domain.model.dayOfWeekLabels
import `in`.koreatech.business.feature.store.dashboard.StoreDashboardViewModel
import `in`.koreatech.business.feature.store.shared.ActiveStoreContext
import `in`.koreatech.business.platform.getCurrentDateString
import `in`.koreatech.business.ui.component.FilledActionButton
import `in`.koreatech.business.ui.component.KoinCard
import `in`.koreatech.business.ui.theme.KoinTheme
import koin_owner_mobile.composeapp.generated.resources.*
import koin_owner_mobile.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TabDashboardContent(
    activeStoreContext: ActiveStoreContext?,
    onNavigateToInsertStore: () -> Unit,
    viewModel: StoreDashboardViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()

    LaunchedEffect(activeStoreContext) {
        viewModel.load(activeStoreContext?.activeStoreId)
    }

    val currentActiveStoreId = uiState.activeStore?.uid?.toString()
    val lifecycleOwner = LocalLifecycleOwner.current
    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME && currentActiveStoreId != null) {
                viewModel.refresh()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "대시보드",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = KoinTheme.colors.neutral800
                )
            },
            actions = {
                if (uiState.activeStore != null) {
                    IconButton(onClick = viewModel::refresh) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(Res.string.refresh),
                            tint = KoinTheme.colors.neutral800Variant
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = KoinTheme.colors.neutral0
            )
        )

        if (uiState.activeStore == null && !uiState.isLoading) {
            EmptyStoreState(onNavigateToInsertStore = onNavigateToInsertStore)
            return
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // 매장 셀렉터 (다중 매장)
            if (uiState.stores.size > 1) {
                StoreSelector(
                    storeName = uiState.activeStore?.name.orEmpty(),
                    storeCount = uiState.stores.size,
                    stores = uiState.stores.map { it.name },
                    activeIndex = uiState.stores.indexOfFirst { it.uid == uiState.activeStore?.uid },
                    onSelectIndex = { idx ->
                        uiState.stores.getOrNull(idx)?.let { viewModel.selectStore(it) }
                    },
                    onAddStore = onNavigateToInsertStore
                )
                Spacer(Modifier.height(12.dp))
            }

            // 매장 헤더 카드 (Hero)
            StoreHeroCard(
                name = uiState.activeStore?.name.orEmpty(),
                phone = uiState.storeDetail?.phone.orEmpty(),
                address = uiState.storeDetail?.address.orEmpty(),
                isDelivery = uiState.storeDetail?.isDelivery == true,
                isCard = uiState.storeDetail?.isCard == true,
                isBank = uiState.storeDetail?.isBank == true
            )
            Spacer(Modifier.height(20.dp))

            // 운영 요약 라벨
            Text(
                text = "운영 요약",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = KoinTheme.colors.neutral500,
                letterSpacing = 0.6.sp,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            // 운영 요약 카드 3장
            val today = remember { getCurrentDateString() }
            val liveEventCount = uiState.events.count { it.isLive(today) }
            val upcomingTitle = uiState.events.firstOrNull { it.isLive(today) }?.title
            val hiddenMenuCount = uiState.menus.flatMap { it.menus }.count { it.isHidden }
            val operatingHoursLabel = formatOperatingHours(uiState.storeDetail?.operatingTimes)

            SummaryCard(
                icon = Icons.Default.LocalOffer,
                iconBg = KoinTheme.colors.primary100,
                iconTint = KoinTheme.colors.primary500,
                title = "진행 중인 이벤트",
                sub = upcomingTitle ?: "진행 중인 이벤트가 없습니다",
                value = liveEventCount.toString(),
                valueColor = if (liveEventCount > 0) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800Variant
            )
            Spacer(Modifier.height(8.dp))
            SummaryCard(
                icon = Icons.Default.VisibilityOff,
                iconBg = KoinTheme.colors.neutral200,
                iconTint = KoinTheme.colors.neutral500,
                title = "숨김 처리된 메뉴",
                sub = "앱에 표시되지 않습니다",
                value = hiddenMenuCount.toString(),
                valueColor = KoinTheme.colors.neutral800Variant
            )
            Spacer(Modifier.height(8.dp))
            SummaryCard(
                icon = Icons.Default.AccessTime,
                iconBg = KoinTheme.colors.neutral200,
                iconTint = KoinTheme.colors.neutral500,
                title = "영업 시간",
                sub = operatingHoursLabel,
                value = null,
                valueColor = KoinTheme.colors.neutral800Variant
            )
            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmptyStoreState(onNavigateToInsertStore: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(32.dp))
                .background(KoinTheme.colors.primary100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                tint = KoinTheme.colors.primary500,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(18.dp))
        Text(
            text = "등록된 매장이 없습니다.",
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = KoinTheme.colors.neutral800
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "매장을 등록하고 KOIN에서\n학생 손님을 만나보세요.",
            fontSize = 13.sp,
            color = KoinTheme.colors.neutral500
        )
        Spacer(Modifier.height(24.dp))
        FilledActionButton(
            text = stringResource(Res.string.store_register_btn),
            onClick = onNavigateToInsertStore,
            modifier = Modifier.width(220.dp)
        )
    }
}

@Composable
private fun StoreSelector(
    storeName: String,
    storeCount: Int,
    stores: List<String>,
    activeIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onAddStore: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(KoinTheme.colors.neutral0)
                .clickable { expanded = !expanded }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(16.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = storeName,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = KoinTheme.colors.neutral800,
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(KoinTheme.colors.neutral200)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${storeCount}개 중",
                    fontSize = 11.sp,
                    color = KoinTheme.colors.neutral500
                )
            }
            Spacer(Modifier.width(6.dp))
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(16.dp)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            stores.forEachIndexed { idx, name ->
                val active = idx == activeIndex
                DropdownMenuItem(
                    text = {
                        Text(
                            text = name,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Medium,
                            color = if (active) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelectIndex(idx)
                    }
                )
            }
            HorizontalDivider(color = KoinTheme.colors.neutral200)
            DropdownMenuItem(
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = KoinTheme.colors.primary500,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "매장 추가 등록",
                            fontWeight = FontWeight.SemiBold,
                            color = KoinTheme.colors.primary500
                        )
                    }
                },
                onClick = {
                    expanded = false
                    onAddStore()
                }
            )
        }
    }
}

@Composable
private fun StoreHeroCard(
    name: String,
    phone: String,
    address: String,
    isDelivery: Boolean,
    isCard: Boolean,
    isBank: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(KoinTheme.colors.primary500)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(KoinTheme.colors.success700)
            )
            Spacer(Modifier.width(6.dp))
            Text(
                text = "영업중",
                fontSize = 11.sp,
                color = Color.White.copy(alpha = 0.85f)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(
            text = name,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        if (phone.isNotEmpty()) {
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(11.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = phone,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
        if (address.isNotEmpty()) {
            Spacer(Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.Top) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White.copy(alpha = 0.85f),
                    modifier = Modifier.size(11.dp).padding(top = 2.dp)
                )
                Spacer(Modifier.width(6.dp))
                Text(
                    text = address,
                    fontSize = 12.sp,
                    color = Color.White.copy(alpha = 0.85f)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (isDelivery) WhiteBadge("배달")
            if (isCard) WhiteBadge("카드")
            if (isBank) WhiteBadge("계좌이체")
        }
    }
}

@Composable
private fun WhiteBadge(label: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White.copy(alpha = 0.18f))
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SummaryCard(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    sub: String,
    value: String?,
    valueColor: Color
) {
    KoinCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = KoinTheme.colors.neutral800
                )
                Text(
                    text = sub,
                    fontSize = 12.sp,
                    color = KoinTheme.colors.neutral500,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            if (value != null) {
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
            }
        }
    }
}

private fun StoreEvent.isLive(todayIso: String): Boolean {
    val start = startDate.take(10)
    val end = endDate.take(10)
    return start <= todayIso && todayIso <= end
}

private fun formatOperatingHours(times: List<OperatingTime>?): String {
    if (times.isNullOrEmpty()) return "정보 없음"
    val opens = times.filterNot { it.isClosed }
    if (opens.isEmpty()) return "휴무"
    val sample = opens.first()
    val sameTime = opens.all { it.openTime == sample.openTime && it.closeTime == sample.closeTime }
    val closedDays = times.filter { it.isClosed }.mapNotNull { dayOfWeekLabels[it.dayOfWeek] }
    val timeLabel = "${sample.openTime.orEmpty()} – ${sample.closeTime.orEmpty()}"
    return when {
        sameTime && closedDays.isEmpty() -> "매일 $timeLabel · 휴무 없음"
        sameTime && closedDays.size == 1 -> "매일 $timeLabel · ${closedDays.first()}요일 휴무"
        sameTime -> "매일 $timeLabel · ${closedDays.joinToString(",")}요일 휴무"
        else -> "요일별 다름"
    }
}
