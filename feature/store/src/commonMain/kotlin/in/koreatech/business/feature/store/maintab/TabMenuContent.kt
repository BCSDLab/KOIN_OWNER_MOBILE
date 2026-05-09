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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.domain.model.MenuItem
import `in`.koreatech.business.feature.store.menu.manage.ManageMenusViewModel
import `in`.koreatech.business.ui.component.KoinAsyncImage
import `in`.koreatech.business.ui.component.KoinButton
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun TabMenuContent(
    onNavigateToMenuEditor: (storeId: String, menuId: String?) -> Unit,
    onNavigateToCategories: (storeId: String) -> Unit,
    viewModel: ManageMenusViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val storeId = uiState.storeId

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

    Box(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = stringResource(Res.string.tab_menu),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KoinTheme.colors.neutral800
                        )
                    }
                },
                actions = {
                    if (!storeId.isNullOrBlank()) {
                        CategoryChipButton(onClick = { onNavigateToCategories(storeId) })
                        Spacer(Modifier.width(8.dp))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KoinTheme.colors.neutral50
                )
            )

            when {
                storeId.isNullOrBlank() -> {
                    EmptyState(
                        title = stringResource(Res.string.store_register_first),
                        sub = null,
                        ctaLabel = null,
                        onCta = null
                    )
                }
                uiState.isLoading && uiState.categories.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KoinTheme.colors.primary500)
                    }
                }
                uiState.categories.isEmpty() -> {
                    EmptyState(
                        title = stringResource(Res.string.no_menu),
                        sub = stringResource(Res.string.menu_empty_sub),
                        ctaLabel = stringResource(Res.string.register_menu_plus),
                        onCta = { onNavigateToMenuEditor(storeId, null) }
                    )
                }
                else -> {
                    MenuList(
                        categories = uiState.categories,
                        onClickMenu = { menu -> onNavigateToMenuEditor(storeId, menu.id.toString()) }
                    )
                }
            }
        }

        if (!storeId.isNullOrBlank() && uiState.categories.isNotEmpty()) {
            ExtendedFloatingActionButton(
                onClick = { onNavigateToMenuEditor(storeId, null) },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 16.dp)
                    .semantics { testTag = "menu_add_fab" },
                containerColor = KoinTheme.colors.primary500,
                contentColor = Color.White,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null
                    )
                },
                text = { Text(text = stringResource(Res.string.menu_add_title)) }
            )
        }
    }
}

@Composable
private fun CategoryChipButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(KoinTheme.colors.neutral50)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.LocalOffer,
            contentDescription = null,
            tint = KoinTheme.colors.neutral800Variant,
            modifier = Modifier.size(13.dp)
        )
        Spacer(Modifier.width(4.dp))
        Text(
            text = stringResource(Res.string.field_category),
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = KoinTheme.colors.neutral800Variant
        )
    }
}

@Composable
private fun MenuList(
    categories: List<MenuCategory>,
    onClickMenu: (MenuItem) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(top = 12.dp, bottom = 88.dp)
    ) {
        categories.forEach { category ->
            item(key = "header_${category.id}") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = category.name,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = KoinTheme.colors.neutral800Variant
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = category.menus.size.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = KoinTheme.colors.neutral500
                    )
                }
            }
            items(category.menus, key = { "menu_${category.id}_${it.id}" }) { menu ->
                MenuRow(menu = menu, onClick = { onClickMenu(menu) })
            }
            item(key = "spacer_${category.id}") {
                Spacer(Modifier.height(12.dp))
            }
        }
    }
}

@Composable
private fun MenuRow(menu: MenuItem, onClick: () -> Unit) {
    val singlePrice = menu.singlePrice
    val optionPriceLabel = stringResource(Res.string.option_price_label)
    val priceLabel = when {
        menu.isSingle && singlePrice != null -> "${formatWon(singlePrice)}원"
        menu.optionPrices.size == 1 -> "${formatWon(menu.optionPrices.first().price)}원"
        menu.optionPrices.isNotEmpty() -> optionPriceLabel
        else -> "-"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(KoinTheme.colors.neutral50)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(KoinTheme.colors.neutral200),
            contentAlignment = Alignment.Center
        ) {
            val firstUrl = menu.imageUrls.firstOrNull()
            if (firstUrl != null) {
                KoinAsyncImage(
                    model = firstUrl,
                    contentDescription = menu.name,
                    modifier = Modifier.size(44.dp),
                    cornerRadius = 8.dp
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = null,
                    tint = KoinTheme.colors.neutral400,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = menu.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = KoinTheme.colors.neutral800
            )
            Text(
                text = priceLabel,
                fontSize = 13.sp,
                fontWeight = if (priceLabel == optionPriceLabel) FontWeight.Medium else FontWeight.Bold,
                color = if (priceLabel == optionPriceLabel) KoinTheme.colors.neutral500 else KoinTheme.colors.primary500,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = KoinTheme.colors.neutral400,
            modifier = Modifier.size(14.dp)
        )
    }
}

@Composable
private fun EmptyState(
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
                imageVector = Icons.Default.Store,
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

private fun formatWon(value: Int): String {
    val s = value.toString()
    val sb = StringBuilder()
    val len = s.length
    for (i in 0 until len) {
        if (i > 0 && (len - i) % 3 == 0) sb.append(',')
        sb.append(s[i])
    }
    return sb.toString()
}
