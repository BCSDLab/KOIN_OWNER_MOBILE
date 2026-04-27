@file:OptIn(
    androidx.compose.material3.ExperimentalMaterial3Api::class,
    androidx.compose.foundation.layout.ExperimentalLayoutApi::class
)

package `in`.koreatech.business.feature.store.event.editor

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.platform.rememberFilePicker
import `in`.koreatech.business.ui.component.BusinessSnackbarHost
import `in`.koreatech.business.ui.component.DesktopTopBar
import `in`.koreatech.business.ui.component.GlobalLoadingOverlay
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
import koin_owner_mobile.composeapp.generated.resources.*
import koin_owner_mobile.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun WriteEventScreen(
    storeId: String,
    eventId: String? = null,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    sidebarActions: StoreSidebarActions = StoreSidebarActions(),
    viewModel: WriteEventViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val pickFile = rememberFilePicker(onFilePicked = viewModel::addImage)
    var selectingStartDate by remember { mutableStateOf(false) }
    var selectingEndDate by remember { mutableStateOf(false) }
    var startYear by remember { mutableStateOf("2026") }
    var startMonth by remember { mutableStateOf("01") }
    var startDay by remember { mutableStateOf("01") }
    var endYear by remember { mutableStateOf("2026") }
    var endMonth by remember { mutableStateOf("01") }
    var endDay by remember { mutableStateOf("01") }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(storeId, eventId) {
        viewModel.init(storeId, eventId)
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            viewModel.clearError()
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            WriteEventSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isDesktop = maxWidth >= 440.dp

        Box(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
            Column(modifier = Modifier.fillMaxSize()) {
                val titleText = if (uiState.isEditMode) "이벤트 수정" else stringResource(Res.string.event_register_title)
                val submitLabel = if (uiState.isEditMode) "수정 완료" else stringResource(Res.string.register)
                if (isDesktop) {
                    DesktopTopBar(
                        title = titleText,
                        breadcrumb = stringResource(Res.string.event_management),
                        subtitle = stringResource(Res.string.event_editor_subtitle),
                        actions = {
                            if (uiState.isEditMode) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "삭제",
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
                                text = submitLabel,
                                onClick = viewModel::submit,
                                isLoading = uiState.isLoading
                            )
                        }
                    )
                } else {
                    // Mobile top bar (Material3 TopAppBar — auto status bar inset)
                    androidx.compose.material3.TopAppBar(
                        title = {
                            Text(
                                text = titleText,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = KoinTheme.colors.neutral800
                            )
                        },
                        navigationIcon = {
                            IconButton(onClick = onNavigateBack) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = stringResource(Res.string.back_navigation),
                                    tint = KoinTheme.colors.neutral800
                                )
                            }
                        },
                        actions = {
                            if (uiState.isEditMode) {
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "삭제",
                                        tint = KoinTheme.colors.danger600
                                    )
                                }
                            }
                        },
                        colors = androidx.compose.material3.TopAppBarDefaults.topAppBarColors(
                            containerColor = KoinTheme.colors.neutral0
                        )
                    )
                }

                if (isDesktop) {
                    // Desktop: 2-column (form cards | image + preview cards) — responsive
                    BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val sidePadding = (this.maxWidth * 0.04f).coerceIn(16.dp, 80.dp)
                        Row(
                            modifier = Modifier
                                .widthIn(max = 1040.dp)
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .padding(horizontal = sidePadding, vertical = 24.dp)
                        ) {
                            // Left: form (cards)
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(Res.string.field_event_title), style = MaterialTheme.typography.labelSmall, color = KoinTheme.colors.neutral500)
                                    KoinTextField(
                                        value = uiState.title,
                                        onValueChange = viewModel::onTitleChanged,
                                        placeholder = stringResource(Res.string.ph_event_title),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(text = stringResource(Res.string.field_event_content), style = MaterialTheme.typography.labelSmall, color = KoinTheme.colors.neutral500)
                                    KoinTextField(
                                        value = uiState.content,
                                        onValueChange = viewModel::onContentChanged,
                                        placeholder = stringResource(Res.string.ph_event_content),
                                        singleLine = false,
                                        minLines = 7,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }

                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(Res.string.event_period_label), style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = KoinTheme.colors.neutral800)
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                                    ) {
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(text = stringResource(Res.string.field_start_date), style = MaterialTheme.typography.labelSmall, color = KoinTheme.colors.neutral500)
                                            Box {
                                                KoinTextField(value = uiState.startDate, onValueChange = {}, readOnly = true, placeholder = stringResource(Res.string.field_start_date), modifier = Modifier.fillMaxWidth())
                                                Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)).clickable { selectingStartDate = true })
                                            }
                                        }
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(text = stringResource(Res.string.field_end_date), style = MaterialTheme.typography.labelSmall, color = KoinTheme.colors.neutral500)
                                            Box {
                                                KoinTextField(value = uiState.endDate, onValueChange = {}, readOnly = true, placeholder = stringResource(Res.string.field_end_date), modifier = Modifier.fillMaxWidth())
                                                Box(modifier = Modifier.matchParentSize().clip(RoundedCornerShape(12.dp)).clickable { selectingEndDate = true })
                                            }
                                        }
                                    }
                                    FlowRow(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        listOf(stringResource(Res.string.period_1week) to 7, stringResource(Res.string.period_2weeks) to 14, stringResource(Res.string.period_1month) to 30, stringResource(Res.string.period_3months) to 90).forEach { (label, days) ->
                                            Surface(
                                                shape = RoundedCornerShape(14.dp),
                                                color = KoinTheme.colors.neutral0,
                                                border = BorderStroke(1.dp, KoinTheme.colors.neutral400),
                                                modifier = Modifier.clickable { viewModel.applyDurationPreset(days) }
                                            ) {
                                                Text(text = label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), style = MaterialTheme.typography.labelSmall, color = KoinTheme.colors.neutral800Variant)
                                            }
                                        }
                                    }
                                }

                                if (uiState.errorMessage.isNotEmpty()) {
                                    KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                                }
                            }

                            Spacer(Modifier.width(24.dp))

                            // Right: image + preview (320dp)
                            Column(
                                modifier = Modifier
                                    .width(320.dp)
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KoinCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(20.dp)) {
                                    val totalImageCount = uiState.existingImageUrls.size + uiState.images.size
                                    Text(text = stringResource(Res.string.event_image_count_of_3, totalImageCount), style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold, color = KoinTheme.colors.neutral800)
                                    EventImageThumbnailRow(
                                        existingUrls = uiState.existingImageUrls,
                                        files = uiState.images,
                                        onAddImage = pickFile,
                                        onRemoveExisting = viewModel::removeExistingImage,
                                        onRemove = viewModel::removeImage
                                    )
                                    Text(text = stringResource(Res.string.image_spec_event), style = MaterialTheme.typography.bodySmall, color = KoinTheme.colors.neutral500)
                                }

                                // App preview card
                                KoinCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(16.dp)) {
                                    Text(text = stringResource(Res.string.app_preview), style = MaterialTheme.typography.labelSmall, color = KoinTheme.colors.neutral500)
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(KoinTheme.colors.neutral100)
                                            .padding(14.dp),
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(56.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(KoinTheme.colors.neutral300),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(Icons.Default.LocalOffer, contentDescription = null, tint = KoinTheme.colors.neutral800Variant, modifier = Modifier.size(24.dp))
                                        }
                                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(10.dp))
                                                    .background(KoinTheme.colors.primary100)
                                                    .padding(horizontal = 7.dp, vertical = 2.dp)
                                            ) {
                                                Text(text = stringResource(Res.string.event_ongoing), style = MaterialTheme.typography.bodySmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold, color = KoinTheme.colors.primary500)
                                            }
                                            Text(
                                                text = uiState.title.ifEmpty { stringResource(Res.string.event_title_preview) },
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                                color = KoinTheme.colors.neutral800,
                                                maxLines = 1
                                            )
                                            Text(
                                                text = "${uiState.startDate.ifEmpty { stringResource(Res.string.field_start_date) }} ~ ${uiState.endDate.ifEmpty { stringResource(Res.string.field_end_date) }}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = KoinTheme.colors.neutral500
                                            )
                                        }
                                    }
                                }
                            }
                        } // Row
                    } // BoxWithConstraints
                } else {
                    // Mobile: single column (existing layout)
                    // Form
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Title field
                        FieldLabel(text = stringResource(Res.string.field_event_title))
                        KoinTextField(
                            value = uiState.title,
                            onValueChange = viewModel::onTitleChanged,
                            placeholder = stringResource(Res.string.ph_event_title),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Content field
                        FieldLabel(text = stringResource(Res.string.field_event_content))
                        KoinTextField(
                            value = uiState.content,
                            onValueChange = viewModel::onContentChanged,
                            placeholder = stringResource(Res.string.ph_event_content),
                            singleLine = false,
                            minLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Date range
                        FieldLabel(text = stringResource(Res.string.field_event_period))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                KoinTextField(
                                    value = uiState.startDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = stringResource(Res.string.ph_start_date),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectingStartDate = true }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                KoinTextField(
                                    value = uiState.endDate,
                                    onValueChange = {},
                                    readOnly = true,
                                    placeholder = stringResource(Res.string.ph_end_date),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Box(
                                    modifier = Modifier
                                        .matchParentSize()
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { selectingEndDate = true }
                                )
                            }
                        }

                        // Period preset chips
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf(
                                stringResource(Res.string.period_1week) to 7,
                                stringResource(Res.string.period_2weeks) to 14,
                                stringResource(Res.string.period_1month) to 30,
                                stringResource(Res.string.period_3months) to 90
                            ).forEach { (label, days) ->
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = KoinTheme.colors.neutral100,
                                    border = BorderStroke(1.dp, KoinTheme.colors.neutral300),
                                    modifier = Modifier.clickable { viewModel.applyDurationPreset(days) }
                                ) {
                                    Text(
                                        text = label,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = KoinTheme.colors.neutral800Variant
                                    )
                                }
                            }
                        }

                        // Image upload
                        val totalImageCount = uiState.existingImageUrls.size + uiState.images.size
                        FieldLabel(text = stringResource(Res.string.event_image_count_of_3, totalImageCount))
                        EventImageThumbnailRow(
                            existingUrls = uiState.existingImageUrls,
                            files = uiState.images,
                            onAddImage = pickFile,
                            onRemoveExisting = viewModel::removeExistingImage,
                            onRemove = viewModel::removeImage
                        )

                        if (uiState.errorMessage.isNotEmpty()) {
                            KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
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
                            text = if (uiState.isEditMode) "수정 완료" else stringResource(Res.string.register),
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

            GlobalLoadingOverlay(
                visible = uiState.isLoading,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } // BoxWithConstraints

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(text = "이벤트 삭제", style = MaterialTheme.typography.headlineMedium) },
            text = { Text(text = "이벤트를 삭제하시겠습니까?", style = MaterialTheme.typography.bodyMedium) },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    viewModel.deleteEvent()
                }) {
                    Text(text = "삭제", color = KoinTheme.colors.danger700, style = MaterialTheme.typography.labelMedium)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(text = stringResource(Res.string.cancel), style = MaterialTheme.typography.labelMedium)
                }
            }
        )
    }

    if (selectingStartDate) {
        EventDatePickerDialog(
            year = startYear,
            month = startMonth,
            day = startDay,
            onYearChanged = { startYear = it },
            onMonthChanged = { startMonth = it },
            onDayChanged = { startDay = it },
            onDismiss = { selectingStartDate = false },
            onConfirm = {
                viewModel.onStartDateChanged(it)
                selectingStartDate = false
            }
        )
    }

    if (selectingEndDate) {
        EventDatePickerDialog(
            year = endYear,
            month = endMonth,
            day = endDay,
            onYearChanged = { endYear = it },
            onMonthChanged = { endMonth = it },
            onDayChanged = { endDay = it },
            onDismiss = { selectingEndDate = false },
            onConfirm = {
                viewModel.onEndDateChanged(it)
                selectingEndDate = false
            }
        )
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = KoinTheme.colors.neutral800Variant
    )
}

@Composable
private fun EventDatePickerDialog(
    year: String,
    month: String,
    day: String,
    onYearChanged: (String) -> Unit,
    onMonthChanged: (String) -> Unit,
    onDayChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = stringResource(Res.string.date_picker_title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = KoinTheme.colors.neutral800Variant
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    KoinTextField(
                        value = year,
                        onValueChange = { onYearChanged(it.filter(Char::isDigit).take(4)) },
                        placeholder = "YYYY",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    KoinTextField(
                        value = month,
                        onValueChange = { onMonthChanged(it.filter(Char::isDigit).take(2)) },
                        placeholder = "MM",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    KoinTextField(
                        value = day,
                        onValueChange = { onDayChanged(it.filter(Char::isDigit).take(2)) },
                        placeholder = "DD",
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm("${year.padStart(4, '0')}-${month.padStart(2, '0')}-${day.padStart(2, '0')}")
                }
            ) {
                Text(stringResource(Res.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.cancel))
            }
        }
    )
}

@Composable
private fun EventImageThumbnailRow(
    existingUrls: List<String>,
    files: List<PlatformFile>,
    onAddImage: () -> Unit,
    onRemoveExisting: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    val total = existingUrls.size + files.size
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        existingUrls.forEachIndexed { index, url ->
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .border(1.dp, KoinTheme.colors.neutral200, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    KoinAsyncImage(
                        model = url,
                        contentDescription = stringResource(Res.string.image_number, index + 1),
                        modifier = Modifier.size(86.dp),
                        cornerRadius = 8.dp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(11.dp))
                        .background(KoinTheme.colors.danger600)
                        .clickable { onRemoveExisting(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.remove_image),
                        tint = KoinTheme.colors.neutral0,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        files.forEachIndexed { index, file ->
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .padding(top = 4.dp, end = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .border(1.dp, KoinTheme.colors.neutral200, RoundedCornerShape(8.dp))
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    KoinAsyncImage(
                        model = file.bytes,
                        contentDescription = file.name,
                        modifier = Modifier.size(86.dp),
                        cornerRadius = 8.dp
                    )
                }
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(11.dp))
                        .background(KoinTheme.colors.danger600)
                        .clickable { onRemove(index) },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.remove_image),
                        tint = KoinTheme.colors.neutral0,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
        if (total < 3) {
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(KoinTheme.colors.neutral100)
                    .border(1.dp, KoinTheme.colors.neutral400, RoundedCornerShape(12.dp))
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
