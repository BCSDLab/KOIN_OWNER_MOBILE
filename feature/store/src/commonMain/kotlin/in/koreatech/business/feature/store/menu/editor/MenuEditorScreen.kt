package `in`.koreatech.business.feature.store.menu.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.platform.rememberFilePicker
import `in`.koreatech.business.ui.component.BusinessSnackbarHost
import `in`.koreatech.business.ui.component.DesktopTopBar
import `in`.koreatech.business.ui.component.GradientActionButton
import `in`.koreatech.business.ui.component.KoinAsyncImage
import `in`.koreatech.business.ui.component.KoinButton
import `in`.koreatech.business.ui.component.KoinButtonVariant
import `in`.koreatech.business.ui.component.KoinCard
import `in`.koreatech.business.ui.component.KoinTextField
import `in`.koreatech.business.ui.component.KoinTextFieldAlert
import `in`.koreatech.business.ui.component.KoinTextFieldAlertType
import `in`.koreatech.business.ui.component.StoreSidebarActions
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun MenuEditorScreen(
    storeId: String,
    menuId: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    sidebarActions: StoreSidebarActions = StoreSidebarActions(),
    viewModel: MenuEditorViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val resolvedError = uiState.errorMessageRes?.let { stringResource(it) } ?: uiState.errorMessage
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val pickFile = rememberFilePicker(onFilePicked = viewModel::addImage)
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(storeId, menuId) {
        viewModel.init(storeId, menuId)
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    text = stringResource(Res.string.menu_delete_dialog_title),
                    style = MaterialTheme.typography.headlineMedium
                )
            },
            text = {
                Text(
                    text = stringResource(Res.string.delete_menu_confirm, uiState.name),
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.deleteMenu()
                    }
                ) {
                    Text(
                        text = stringResource(Res.string.delete),
                        style = MaterialTheme.typography.labelMedium,
                        color = KoinTheme.colors.danger700
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(
                        text = stringResource(Res.string.cancel),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
        )
    }

    LaunchedEffect(resolvedError) {
        if (resolvedError.isNotEmpty()) {
            snackbarHostState.showSnackbar(resolvedError)
            viewModel.clearError()
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            MenuEditorSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isDesktop = maxWidth >= 440.dp

        Box(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isDesktop) {
                    DesktopTopBar(
                        title = if (uiState.isEditMode) stringResource(Res.string.menu_edit_title) else stringResource(Res.string.menu_add_title),
                        breadcrumb = stringResource(Res.string.manage_menus_title),
                        subtitle = stringResource(Res.string.menu_editor_subtitle),
                        actions = {
                            if (uiState.isEditMode) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(Res.string.delete),
                                        tint = KoinTheme.colors.danger600
                                    )
                                }
                            }
                            KoinButton(
                                text = stringResource(Res.string.cancel),
                                onClick = onNavigateBack,
                                variant = KoinButtonVariant.Outlined
                            )
                            KoinButton(
                                text = if (uiState.isEditMode) stringResource(Res.string.edit_complete) else stringResource(Res.string.register),
                                onClick = viewModel::submit,
                                isLoading = uiState.isLoading
                            )
                        }
                    )
                } else {
                    // Mobile top bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KoinTheme.colors.neutral50)
                            .statusBarsPadding()
                            .padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(Res.string.back_navigation),
                                tint = KoinTheme.colors.neutral800
                            )
                        }
                        Text(
                            text = if (uiState.isEditMode) stringResource(Res.string.menu_edit_title) else stringResource(Res.string.menu_add_title),
                            style = MaterialTheme.typography.headlineMedium,
                            color = KoinTheme.colors.neutral800,
                            modifier = Modifier.weight(1f)
                        )
                        if (uiState.isEditMode) {
                            IconButton(onClick = { showDeleteDialog = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(Res.string.delete),
                                    tint = KoinTheme.colors.danger600
                                )
                            }
                        }
                    }
                }

                if (isDesktop) {
                    // Desktop: 2-column (form | image panel) with cards — responsive
                    BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val sidePadding = (this.maxWidth * 0.04f).coerceIn(16.dp, 80.dp)
                        Row(
                            modifier = Modifier
                                .widthIn(max = 1000.dp)
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(horizontal = sidePadding, vertical = 24.dp)
                        ) {
                            // Left: form (cards stacked)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    DesktopCardLabel(stringResource(Res.string.field_basic_info))
                                    FieldSection(label = stringResource(Res.string.field_menu_name)) {
                                        KoinTextField(
                                            value = uiState.name,
                                            onValueChange = viewModel::onNameChanged,
                                            placeholder = stringResource(Res.string.ph_menu_name),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    FieldSection(label = stringResource(Res.string.field_menu_description)) {
                                        KoinTextField(
                                            value = uiState.description,
                                            onValueChange = viewModel::onDescriptionChanged,
                                            placeholder = stringResource(Res.string.ph_menu_description),
                                            singleLine = false,
                                            minLines = 3,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    if (uiState.menuCategories.isNotEmpty()) {
                                        FieldSection(label = stringResource(Res.string.field_category)) {
                                            CategoryChipsSection(
                                                categories = uiState.menuCategories,
                                                selectedIds = uiState.selectedCategoryIds,
                                                onToggle = viewModel::onCategoryToggled
                                            )
                                        }
                                    }
                                }

                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        DesktopCardLabel(
                                            if (uiState.optionPrices.isEmpty()) stringResource(Res.string.field_price) else stringResource(Res.string.field_price_option),
                                            modifier = Modifier.weight(1f)
                                        )
                                        androidx.compose.material3.TextButton(
                                            onClick = viewModel::addOptionPrice
                                        ) {
                                            Text(
                                                text = stringResource(Res.string.add_option),
                                                style = MaterialTheme.typography.labelMedium,
                                                color = KoinTheme.colors.primary500
                                            )
                                        }
                                    }
                                    if (uiState.optionPrices.isEmpty()) {
                                        KoinTextField(
                                            value = uiState.singlePrice,
                                            onValueChange = viewModel::onSinglePriceChanged,
                                            placeholder = "0",
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    } else {
                                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                            uiState.optionPrices.forEachIndexed { index, draft ->
                                                Row(
                                                    modifier = Modifier.fillMaxWidth(),
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                    verticalAlignment = Alignment.CenterVertically
                                                ) {
                                                    KoinTextField(
                                                        value = draft.option,
                                                        onValueChange = { viewModel.onOptionNameChanged(index, it) },
                                                        placeholder = stringResource(Res.string.ph_option_name),
                                                        modifier = Modifier.weight(1f)
                                                    )
                                                    KoinTextField(
                                                        value = draft.price,
                                                        onValueChange = { viewModel.onPriceChanged(index, it) },
                                                        placeholder = stringResource(Res.string.field_price),
                                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                                        modifier = Modifier.width(140.dp)
                                                    )
                                                    IconButton(
                                                        onClick = { viewModel.removeOptionPrice(index) },
                                                        modifier = Modifier.size(40.dp)
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Close,
                                                            contentDescription = stringResource(Res.string.delete),
                                                            tint = KoinTheme.colors.danger600,
                                                            modifier = Modifier.size(18.dp)
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }

                                if (resolvedError.isNotEmpty()) {
                                    KoinTextFieldAlert(message = resolvedError, type = KoinTextFieldAlertType.Error)
                                }
                            }

                            Spacer(Modifier.width(24.dp))

                            // Right: image panel (300dp)
                            Column(
                                modifier = Modifier
                                    .width(300.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KoinCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                                    DesktopCardLabel(stringResource(Res.string.image_count_of_3, uiState.existingImageUrls.size + uiState.pendingImages.size))
                                    ImageThumbnailRow(
                                        existingUrls = uiState.existingImageUrls,
                                        pendingFiles = uiState.pendingImages,
                                        onAddImage = pickFile,
                                        onRemoveExisting = viewModel::removeExistingImage,
                                        onRemovePending = viewModel::removePendingImage
                                    )
                                    Text(
                                        text = stringResource(Res.string.image_spec_menu),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KoinTheme.colors.neutral500
                                    )
                                }
                            }
                        } // Row
                    } // BoxWithConstraints
                } else {
                    // Mobile: single column (existing layout)
                    // Form content
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Name field
                        FieldSection(label = stringResource(Res.string.field_menu_name)) {
                            KoinTextField(
                                value = uiState.name,
                                onValueChange = viewModel::onNameChanged,
                                placeholder = stringResource(Res.string.ph_menu_name),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Description field
                        FieldSection(label = stringResource(Res.string.field_menu_description)) {
                            KoinTextField(
                                value = uiState.description,
                                onValueChange = viewModel::onDescriptionChanged,
                                placeholder = stringResource(Res.string.ph_menu_description),
                                singleLine = false,
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Category chips
                        if (uiState.menuCategories.isNotEmpty()) {
                            FieldSection(label = stringResource(Res.string.field_category)) {
                                CategoryChipsSection(
                                    categories = uiState.menuCategories,
                                    selectedIds = uiState.selectedCategoryIds,
                                    onToggle = viewModel::onCategoryToggled
                                )
                            }
                        }

                        // Price (single or options)
                        FieldSection(label = if (uiState.optionPrices.isEmpty()) stringResource(Res.string.field_price) else stringResource(Res.string.field_price_option)) {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (uiState.optionPrices.isEmpty()) {
                                    KoinTextField(
                                        value = uiState.singlePrice,
                                        onValueChange = viewModel::onSinglePriceChanged,
                                        placeholder = stringResource(Res.string.ph_price),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                } else {
                                    uiState.optionPrices.forEachIndexed { index, draft ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            KoinTextField(
                                                value = draft.option,
                                                onValueChange = { viewModel.onOptionNameChanged(index, it) },
                                                placeholder = stringResource(Res.string.ph_option_name),
                                                modifier = Modifier.weight(0.45f)
                                            )
                                            KoinTextField(
                                                value = draft.price,
                                                onValueChange = { viewModel.onPriceChanged(index, it) },
                                                placeholder = stringResource(Res.string.field_price),
                                                keyboardOptions = KeyboardOptions(
                                                    keyboardType = KeyboardType.Number
                                                ),
                                                modifier = Modifier.weight(0.45f)
                                            )
                                            IconButton(
                                                onClick = { viewModel.removeOptionPrice(index) },
                                                modifier = Modifier.size(40.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Close,
                                                    contentDescription = stringResource(Res.string.delete),
                                                    tint = KoinTheme.colors.danger600,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    }
                                }

                                // Add option button (always visible)
                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(KoinTheme.colors.neutral300)
                                        .clickable(onClick = viewModel::addOptionPrice)
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(Res.string.add_option_btn),
                                        tint = KoinTheme.colors.neutral500,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = stringResource(Res.string.add_option_btn),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = KoinTheme.colors.neutral500
                                    )
                                }
                            }
                        }

                        FieldSection(
                            label = stringResource(Res.string.image_count_of_3, uiState.existingImageUrls.size + uiState.pendingImages.size)
                        ) {
                            ImageThumbnailRow(
                                existingUrls = uiState.existingImageUrls,
                                pendingFiles = uiState.pendingImages,
                                onAddImage = pickFile,
                                onRemoveExisting = viewModel::removeExistingImage,
                                onRemovePending = viewModel::removePendingImage
                            )
                        }

                        if (resolvedError.isNotEmpty()) {
                            KoinTextFieldAlert(message = resolvedError, type = KoinTextFieldAlertType.Error)
                        }
                    }

                    // Fixed bottom button (mobile only)
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(KoinTheme.colors.neutral50)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        GradientActionButton(
                            text = if (uiState.isEditMode) stringResource(Res.string.edit) else stringResource(Res.string.register),
                            onClick = viewModel::submit,
                            isLoading = uiState.isLoading
                        )
                    }
                } // else (mobile)
            }

            BusinessSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    } // BoxWithConstraints
}

@Composable
private fun FieldSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = KoinTheme.colors.neutral500
        )
        content()
    }
}

@Composable
private fun DesktopCardLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = KoinTheme.colors.neutral800,
        modifier = modifier
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class, androidx.compose.foundation.layout.ExperimentalLayoutApi::class)
@Composable
private fun CategoryChipsSection(
    categories: List<MenuCategory>,
    selectedIds: List<Int>,
    onToggle: (Int) -> Unit
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        categories.forEach { category ->
            val selected = category.id in selectedIds
            FilterChip(
                selected = selected,
                onClick = { onToggle(category.id) },
                label = {
                    Text(
                        text = category.name,
                        style = MaterialTheme.typography.bodySmall
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = KoinTheme.colors.primary500.copy(alpha = 0.12f),
                    selectedLabelColor = KoinTheme.colors.primary500
                )
            )
        }
    }
}

@Composable
private fun ImageThumbnailRow(
    existingUrls: List<String>,
    pendingFiles: List<PlatformFile>,
    onAddImage: () -> Unit,
    onRemoveExisting: (Int) -> Unit,
    onRemovePending: (Int) -> Unit
) {
    val total = existingUrls.size + pendingFiles.size
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        existingUrls.forEachIndexed { index, url ->
            ImageThumbnailBox(
                content = {
                    KoinAsyncImage(
                        model = url,
                        contentDescription = stringResource(Res.string.image_number, index + 1),
                        modifier = Modifier.size(90.dp),
                        cornerRadius = 8.dp
                    )
                },
                onRemove = { onRemoveExisting(index) }
            )
        }
        pendingFiles.forEachIndexed { index, file ->
            ImageThumbnailBox(
                content = {
                    KoinAsyncImage(
                        model = file.bytes,
                        contentDescription = file.name,
                        modifier = Modifier.size(90.dp),
                        cornerRadius = 8.dp
                    )
                },
                onRemove = { onRemovePending(index) }
            )
        }
        if (total < 3) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KoinTheme.colors.neutral100)
                    .border(
                        1.dp,
                        KoinTheme.colors.neutral400,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable(onClick = onAddImage),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = stringResource(Res.string.add_image),
                        tint = KoinTheme.colors.primary500,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = stringResource(Res.string.add_image),
                        fontSize = 10.sp,
                        color = KoinTheme.colors.primary500
                    )
                }
            }
        }
    }
}

@Composable
private fun ImageThumbnailBox(
    content: @Composable () -> Unit,
    onRemove: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(90.dp)
            .padding(top = 4.dp, end = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .border(1.dp, KoinTheme.colors.neutral300, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) {
            content()
        }
        Box(
            modifier = Modifier
                .size(22.dp)
                .align(Alignment.TopEnd)
                .clip(RoundedCornerShape(11.dp))
                .background(KoinTheme.colors.danger600)
                .clickable(onClick = onRemove),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.remove_image),
                tint = KoinTheme.colors.neutral50,
                modifier = Modifier.size(14.dp)
            )
        }
    }
}
