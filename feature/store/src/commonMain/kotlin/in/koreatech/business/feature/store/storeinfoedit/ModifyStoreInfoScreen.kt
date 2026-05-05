@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.store.storeinfoedit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
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
import `in`.koreatech.business.domain.model.OperatingTime
import `in`.koreatech.business.domain.model.dayOfWeekLabels
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
import `in`.koreatech.business.ui.component.ShopPhoneVisualTransformation
import `in`.koreatech.business.ui.component.StoreSidebarActions
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun ModifyStoreInfoScreen(
    storeId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    sidebarActions: StoreSidebarActions = StoreSidebarActions(),
    viewModel: StoreInfoEditViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    var timePickerTarget by remember { mutableStateOf<Pair<Int, Boolean>?>(null) }
    val pickFile = rememberFilePicker(onFilePicked = viewModel::addImage)

    LaunchedEffect(storeId) {
        viewModel.load(storeId)
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            viewModel.clearError()
        }
    }

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            StoreInfoEditSideEffect.NavigateBack -> onNavigateBack()
        }
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isDesktop = maxWidth >= 440.dp

        Box(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
            Column(modifier = Modifier.fillMaxSize()) {
                if (isDesktop) {
                    DesktopTopBar(
                        title = stringResource(Res.string.modify_store_info_title),
                        subtitle = stringResource(Res.string.modify_store_info_subtitle),
                        actions = {
                            KoinButton(
                                text = stringResource(Res.string.cancel),
                                onClick = onNavigateBack,
                                variant = KoinButtonVariant.Outlined
                            )
                            KoinButton(
                                text = stringResource(Res.string.save),
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
                            text = stringResource(Res.string.modify_store_info_title),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = KoinTheme.colors.neutral800,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                if (uiState.isLoading && uiState.name.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = KoinTheme.colors.primary500)
                    }
                } else if (isDesktop) {
                    // Desktop: 2-column with cards — responsive
                    BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        val sidePadding = (this.maxWidth * 0.04f).coerceIn(16.dp, 80.dp)
                        Row(
                            modifier = Modifier
                                .widthIn(max = 1100.dp)
                                .align(Alignment.TopCenter)
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .padding(horizontal = sidePadding, vertical = 24.dp)
                        ) {
                            // Left column: basic info + payments
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(Res.string.field_basic_info), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = KoinTheme.colors.neutral800)
                                    FieldSection(label = stringResource(Res.string.field_store_name_label)) {
                                        KoinTextField(value = uiState.name, onValueChange = {}, enabled = false, modifier = Modifier.fillMaxWidth())
                                    }
                                    FieldSection(label = stringResource(Res.string.field_phone)) {
                                        KoinTextField(
                                            value = uiState.phone,
                                            onValueChange = viewModel::onPhoneChanged,
                                            placeholder = stringResource(Res.string.ph_phone),
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                            visualTransformation = ShopPhoneVisualTransformation,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    FieldSection(label = stringResource(Res.string.field_address)) {
                                        KoinTextField(value = uiState.address, onValueChange = viewModel::onAddressChanged, placeholder = stringResource(Res.string.ph_address), modifier = Modifier.fillMaxWidth())
                                    }
                                    FieldSection(label = stringResource(Res.string.field_store_description)) {
                                        KoinTextField(value = uiState.description, onValueChange = viewModel::onDescriptionChanged, placeholder = stringResource(Res.string.ph_store_description), singleLine = false, minLines = 3, modifier = Modifier.fillMaxWidth())
                                    }
                                    FieldSection(label = stringResource(Res.string.field_store_images)) {
                                        StoreImageThumbnailRow(
                                            existingUrls = uiState.existingImageUrls,
                                            pendingFiles = uiState.pendingImages,
                                            onAddImage = pickFile,
                                            onRemoveExisting = viewModel::removeExistingImage,
                                            onRemovePending = viewModel::removePendingImage
                                        )
                                    }
                                }

                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(Res.string.section_payment_delivery), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = KoinTheme.colors.neutral800)
                                    ServiceToggleRow(label = stringResource(Res.string.delivery_available), checked = uiState.isDelivery, onCheckedChange = { viewModel.onToggleDelivery() })
                                    HorizontalDivider(color = KoinTheme.colors.neutral400)
                                    ServiceToggleRow(label = stringResource(Res.string.service_card_payment), checked = uiState.isCard, onCheckedChange = { viewModel.onToggleCard() })
                                    HorizontalDivider(color = KoinTheme.colors.neutral400)
                                    ServiceToggleRow(label = stringResource(Res.string.service_bank_transfer), checked = uiState.isBank, onCheckedChange = { viewModel.onToggleBank() })
                                }

                                if (uiState.errorMessage.isNotEmpty()) {
                                    KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                                }
                            }

                            Spacer(Modifier.width(20.dp))

                            // Right column: operating hours
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState()),
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                KoinCard(modifier = Modifier.fillMaxWidth()) {
                                    Text(text = stringResource(Res.string.section_operating_hours), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = KoinTheme.colors.neutral800)
                                    uiState.operatingTimes.forEachIndexed { index, time ->
                                        OperatingTimeRow(
                                            time = time,
                                            onToggle = { viewModel.onOperatingTimeToggle(index) },
                                            onOpenTimeClick = { timePickerTarget = index to true },
                                            onCloseTimeClick = { timePickerTarget = index to false }
                                        )
                                    }
                                }
                            }
                        } // Row
                    } // BoxWithConstraints
                } else {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(scrollState)
                            .navigationBarsPadding()
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Store name (read-only)
                        FieldSection(label = stringResource(Res.string.field_store_name_label)) {
                            KoinTextField(
                                value = uiState.name,
                                onValueChange = {},
                                enabled = false,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Phone
                        FieldSection(label = stringResource(Res.string.field_phone)) {
                            KoinTextField(
                                value = uiState.phone,
                                onValueChange = viewModel::onPhoneChanged,
                                placeholder = stringResource(Res.string.ph_phone),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                visualTransformation = ShopPhoneVisualTransformation,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Address
                        FieldSection(label = stringResource(Res.string.field_address)) {
                            KoinTextField(
                                value = uiState.address,
                                onValueChange = viewModel::onAddressChanged,
                                placeholder = stringResource(Res.string.ph_address),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Description
                        FieldSection(label = stringResource(Res.string.field_store_description)) {
                            KoinTextField(
                                value = uiState.description,
                                onValueChange = viewModel::onDescriptionChanged,
                                placeholder = stringResource(Res.string.ph_store_description),
                                singleLine = false,
                                minLines = 3,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Store images
                        FieldSection(label = stringResource(Res.string.field_store_images)) {
                            StoreImageThumbnailRow(
                                existingUrls = uiState.existingImageUrls,
                                pendingFiles = uiState.pendingImages,
                                onAddImage = pickFile,
                                onRemoveExisting = viewModel::removeExistingImage,
                                onRemovePending = viewModel::removePendingImage
                            )
                        }

                        HorizontalDivider(color = KoinTheme.colors.neutral400)

                        // Service options
                        SectionHeader(stringResource(Res.string.section_payment_options))

                        ServiceToggleRow(
                            label = stringResource(Res.string.delivery_available),
                            checked = uiState.isDelivery,
                            onCheckedChange = { viewModel.onToggleDelivery() }
                        )
                        ServiceToggleRow(
                            label = stringResource(Res.string.service_card_payment),
                            checked = uiState.isCard,
                            onCheckedChange = { viewModel.onToggleCard() }
                        )
                        ServiceToggleRow(
                            label = stringResource(Res.string.service_bank_transfer),
                            checked = uiState.isBank,
                            onCheckedChange = { viewModel.onToggleBank() }
                        )

                        HorizontalDivider(color = KoinTheme.colors.neutral400)

                        // Operating hours
                        SectionHeader(stringResource(Res.string.section_operating_hours))

                        uiState.operatingTimes.forEachIndexed { index, time ->
                            OperatingTimeRow(
                                time = time,
                                onToggle = { viewModel.onOperatingTimeToggle(index) },
                                onOpenTimeClick = { timePickerTarget = index to true },
                                onCloseTimeClick = { timePickerTarget = index to false }
                            )
                        }

                        if (uiState.errorMessage.isNotEmpty()) {
                            KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        GradientActionButton(
                            text = stringResource(Res.string.save),
                            onClick = viewModel::submit,
                            isLoading = uiState.isLoading
                        )
                    }
                }
            }

            BusinessSnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            GlobalLoadingOverlay(
                visible = uiState.isLoading && uiState.name.isNotEmpty(),
                modifier = Modifier.align(Alignment.Center)
            )
        }
    } // BoxWithConstraints

    timePickerTarget?.let { (index, isOpen) ->
        TimePickerFieldDialog(
            initialValue = uiState.operatingTimes.getOrNull(index)?.let {
                if (isOpen) it.openTime else it.closeTime
            }.orEmpty(),
            onDismiss = { timePickerTarget = null },
            onConfirm = { value ->
                if (isOpen) {
                    viewModel.onOperatingOpenTimeChanged(index, value)
                } else {
                    viewModel.onOperatingCloseTimeChanged(index, value)
                }
                timePickerTarget = null
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 15.sp,
        fontWeight = FontWeight.SemiBold,
        color = KoinTheme.colors.neutral800
    )
}

@Composable
private fun ServiceToggleRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = KoinTheme.colors.neutral800,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedTrackColor = KoinTheme.colors.primary500
            )
        )
    }
}

@Composable
private fun OperatingTimeRow(
    time: OperatingTime,
    onToggle: () -> Unit,
    onOpenTimeClick: () -> Unit,
    onCloseTimeClick: () -> Unit
) {
    val label = dayOfWeekLabels[time.dayOfWeek] ?: time.dayOfWeek

    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = KoinTheme.colors.neutral800Variant,
            modifier = Modifier.width(24.dp)
        )
        Switch(
            checked = !time.isClosed,
            onCheckedChange = { onToggle() },
            colors = SwitchDefaults.colors(checkedTrackColor = KoinTheme.colors.primary500)
        )
        if (!time.isClosed) {
            Spacer(modifier = Modifier.width(8.dp))
            KoinTextField(
                value = time.openTime.orEmpty(),
                onValueChange = {},
                placeholder = stringResource(Res.string.ph_time_start),
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onOpenTimeClick)
            )
            Text(
                text = "~",
                modifier = Modifier.padding(horizontal = 4.dp),
                color = KoinTheme.colors.neutral800Variant
            )
            KoinTextField(
                value = time.closeTime.orEmpty(),
                onValueChange = {},
                placeholder = stringResource(Res.string.ph_time_end),
                readOnly = true,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onCloseTimeClick)
            )
        } else {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(Res.string.closed),
                fontSize = 14.sp,
                color = KoinTheme.colors.neutral800Variant
            )
        }
    }
}

@Composable
private fun TimePickerFieldDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    val initialHour = initialValue.substringBefore(':').toIntOrNull() ?: 9
    val initialMinute = initialValue.substringAfter(':', "00").toIntOrNull() ?: 0
    val state = rememberTimePickerState(
        initialHour = initialHour,
        initialMinute = initialMinute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        text = {
            TimePicker(state = state)
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm("${state.hour.toString().padStart(2, '0')}:${state.minute.toString().padStart(2, '0')}")
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
private fun StoreImageThumbnailRow(
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
            StoreImageBox(
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
            StoreImageBox(
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

@Composable
private fun StoreImageBox(
    content: @Composable () -> Unit,
    onRemove: () -> Unit
) {
    Box(modifier = Modifier.size(90.dp).padding(top = 4.dp, end = 4.dp)) {
        Box(
            modifier = Modifier
                .size(86.dp)
                .border(1.dp, KoinTheme.colors.neutral300, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
        ) { content() }
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

@Composable
private fun FieldSection(
    label: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = KoinTheme.colors.neutral800Variant
        )
        content()
    }
}
