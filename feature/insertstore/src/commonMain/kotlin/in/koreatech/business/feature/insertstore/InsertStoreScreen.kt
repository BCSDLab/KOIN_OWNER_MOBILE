package `in`.koreatech.business.feature.insertstore

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import `in`.koreatech.business.domain.model.dayOfWeekLabels
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.platform.rememberFilePicker
import `in`.koreatech.business.ui.component.GradientActionButton
import `in`.koreatech.business.ui.component.KoinAsyncImage
import `in`.koreatech.business.ui.component.KoinLogo
import `in`.koreatech.business.ui.component.KoinTextField
import `in`.koreatech.business.ui.component.KoinTextFieldAlert
import `in`.koreatech.business.ui.component.KoinTextFieldAlertType
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun InsertStoreScreen(
    onNavigateBack: () -> Unit,
    onNavigateToStoreMain: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: InsertStoreViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val navController = rememberNavController()

    LaunchedEffect(navController, uiState.step) {
        syncInsertStoreRoute(
            navController = navController,
            route = uiState.step.route
        )
    }

    BackHandler {
        val navigated = viewModel.navigateBack()
        if (!navigated) onNavigateBack()
    }

    NavHost(
        navController = navController,
        startDestination = InsertStoreStep.Start.route
    ) {
        composable(InsertStoreStep.Start.route) {
            StartStep(
                onNext = viewModel::navigateNext,
                onClose = onNavigateBack
            )
        }
        composable(InsertStoreStep.SelectCategory.route) {
            SelectCategoryStep(
                uiState = uiState,
                onCategorySelected = viewModel::onCategorySelected,
                onNext = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }
        composable(InsertStoreStep.BasicInfo.route) {
            BasicInfoStep(
                uiState = uiState,
                onNameChanged = viewModel::onNameChanged,
                onAddressChanged = viewModel::onAddressChanged,
                onPhoneChanged = viewModel::onPhoneChanged,
                onAddCoverImage = viewModel::addCoverImage,
                onRemoveCoverImage = viewModel::removeCoverImage,
                onNext = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }
        composable(InsertStoreStep.DetailInfo.route) {
            DetailInfoStep(
                uiState = uiState,
                onToggleCard = viewModel::onToggleCard,
                onToggleBank = viewModel::onToggleBank,
                onToggleDelivery = viewModel::onToggleDelivery,
                onDeliveryPriceChanged = viewModel::onDeliveryPriceChanged,
                onDescriptionChanged = viewModel::onDescriptionChanged,
                onOperatingTimeToggle = viewModel::onOperatingTimeToggle,
                onOpenTimeChanged = viewModel::onOperatingOpenTimeChanged,
                onCloseTimeChanged = viewModel::onOperatingCloseTimeChanged,
                onNext = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }
        composable(InsertStoreStep.FinalCheck.route) {
            FinalCheckStep(
                uiState = uiState,
                onSubmit = viewModel::navigateNext,
                onBack = { viewModel.navigateBack() }
            )
        }
        composable(InsertStoreStep.Complete.route) {
            CompleteStep(
                onNavigateToStoreMain = onNavigateToStoreMain
            )
        }
    }
}

private suspend fun syncInsertStoreRoute(
    navController: NavHostController,
    route: String
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (currentRoute == route) return

    val popped = navController.popBackStack(route, inclusive = false)
    if (!popped) {
        navController.navigate(route) {
            launchSingleTop = true
        }
    }
}

// --- Start ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun StartStep(
    onNext: () -> Unit,
    onClose: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.insert_store_screen_title)) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = stringResource(Res.string.close))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral50)
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        StepCenteredBox(paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.weight(2f))
                KoinLogo(colored = true)
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = stringResource(Res.string.insert_store_main_title),
                    textAlign = TextAlign.Center,
                    style = KoinTheme.typography.bold28,
                    color = KoinTheme.colors.neutral800
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(Res.string.insert_store_main_desc),
                    textAlign = TextAlign.Center,
                    style = KoinTheme.typography.regular14,
                    color = KoinTheme.colors.neutral700
                )
                Spacer(modifier = Modifier.weight(3f))
                GradientActionButton(
                    text = stringResource(Res.string.insert_store_start),
                    onClick = onNext
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- Select Category ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SelectCategoryStep(
    uiState: InsertStoreUiState,
    onCategorySelected: (Int, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.select_category_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back_navigation))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral50)
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KoinTheme.colors.primary500)
            }
        } else {
            StepCenteredBox(paddingValues) {
                Column(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.categories) { category ->
                            val isSelected = category.id == uiState.selectedCategoryId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) {
                                            KoinTheme.colors.primary600.copy(alpha = 0.3f)
                                        } else {
                                            KoinTheme.colors.neutral100
                                        }
                                    )
                                    .clickable { onCategorySelected(category.id, category.name) }
                                    .padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category.name,
                                    style = KoinTheme.typography.medium14,
                                    color = if (isSelected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800,
                                    modifier = Modifier.weight(1f)
                                )
                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = null,
                                        tint = KoinTheme.colors.primary500,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                    if (uiState.errorMessage.isNotEmpty()) {
                        KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                    }
                    GradientActionButton(
                        text = stringResource(Res.string.next),
                        onClick = onNext,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// --- Basic Info ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BasicInfoStep(
    uiState: InsertStoreUiState,
    onNameChanged: (String) -> Unit,
    onAddressChanged: (String) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onAddCoverImage: (PlatformFile) -> Unit,
    onRemoveCoverImage: (Int) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    val pickFile = rememberFilePicker(onFilePicked = onAddCoverImage)
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.field_basic_info)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back_navigation))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral50)
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        StepCenteredBox(paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = stringResource(Res.string.selected_category, uiState.selectedCategoryName),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = KoinTheme.colors.primary500
                )

                FieldLabel(stringResource(Res.string.field_store_name_label))
                KoinTextField(
                    value = uiState.name,
                    onValueChange = onNameChanged,
                    placeholder = stringResource(Res.string.ph_store_name),
                    modifier = Modifier.fillMaxWidth()
                )

                FieldLabel(stringResource(Res.string.field_address))
                KoinTextField(
                    value = uiState.address,
                    onValueChange = onAddressChanged,
                    placeholder = stringResource(Res.string.ph_store_address),
                    modifier = Modifier.fillMaxWidth()
                )

                FieldLabel(stringResource(Res.string.field_phone))
                KoinTextField(
                    value = uiState.phone,
                    onValueChange = onPhoneChanged,
                    placeholder = "01000000000",
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                FieldLabel(stringResource(Res.string.field_store_images))
                InsertStoreCoverImageRow(
                    coverImages = uiState.coverImages,
                    onAddImage = pickFile,
                    onRemoveImage = onRemoveCoverImage
                )

                if (uiState.errorMessage.isNotEmpty()) {
                    KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                }

                Spacer(modifier = Modifier.height(8.dp))

                GradientActionButton(text = stringResource(Res.string.next), onClick = onNext)

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- Detail Info ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DetailInfoStep(
    uiState: InsertStoreUiState,
    onToggleCard: () -> Unit,
    onToggleBank: () -> Unit,
    onToggleDelivery: () -> Unit,
    onDeliveryPriceChanged: (String) -> Unit,
    onDescriptionChanged: (String) -> Unit,
    onOperatingTimeToggle: (Int) -> Unit,
    onOpenTimeChanged: (Int, String) -> Unit,
    onCloseTimeChanged: (Int, String) -> Unit,
    onNext: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.detail_info_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back_navigation))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral50)
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        StepCenteredBox(paddingValues) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // Payment methods
                FieldLabel(stringResource(Res.string.field_payment))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(stringResource(Res.string.payment_card), uiState.isCardOk, onToggleCard)
                    FilterChip(stringResource(Res.string.payment_bank), uiState.isBankOk, onToggleBank)
                }

                // Delivery
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(Res.string.delivery_available), fontSize = 14.sp, color = KoinTheme.colors.neutral800, modifier = Modifier.weight(1f))
                    Switch(
                        checked = uiState.isDeliveryOk,
                        onCheckedChange = { onToggleDelivery() },
                        colors = SwitchDefaults.colors(checkedTrackColor = KoinTheme.colors.primary500)
                    )
                }
                if (uiState.isDeliveryOk) {
                    KoinTextField(
                        value = uiState.deliveryPrice,
                        onValueChange = onDeliveryPriceChanged,
                        placeholder = stringResource(Res.string.ph_delivery_price),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                // Description
                FieldLabel(stringResource(Res.string.field_description))
                KoinTextField(
                    value = uiState.description,
                    onValueChange = onDescriptionChanged,
                    placeholder = stringResource(Res.string.ph_store_intro),
                    singleLine = false,
                    minLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                // Operating hours
                FieldLabel(stringResource(Res.string.field_operating_time))
                uiState.operatingTimes.forEachIndexed { index, time ->
                    val label = dayOfWeekLabels[time.dayOfWeek] ?: time.dayOfWeek
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = label,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = KoinTheme.colors.neutral700,
                            modifier = Modifier.width(28.dp)
                        )
                        Switch(
                            checked = !time.isClosed,
                            onCheckedChange = { onOperatingTimeToggle(index) },
                            colors = SwitchDefaults.colors(checkedTrackColor = KoinTheme.colors.primary500)
                        )
                        if (!time.isClosed) {
                            Spacer(modifier = Modifier.width(8.dp))
                            KoinTextField(
                                value = time.openTime.orEmpty(),
                                onValueChange = { onOpenTimeChanged(index, it) },
                                placeholder = stringResource(Res.string.ph_time_start),
                                modifier = Modifier.weight(1f)
                            )
                            Text(" ~ ", color = KoinTheme.colors.neutral700)
                            KoinTextField(
                                value = time.closeTime.orEmpty(),
                                onValueChange = { onCloseTimeChanged(index, it) },
                                placeholder = stringResource(Res.string.ph_time_end),
                                modifier = Modifier.weight(1f)
                            )
                        } else {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(stringResource(Res.string.closed), fontSize = 14.sp, color = KoinTheme.colors.neutral700)
                        }
                    }
                }

                if (uiState.errorMessage.isNotEmpty()) {
                    KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                }

                Spacer(modifier = Modifier.height(8.dp))
                GradientActionButton(text = stringResource(Res.string.next), onClick = onNext)
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

// --- Final Check ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FinalCheckStep(
    uiState: InsertStoreUiState,
    onSubmit: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.final_check_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.back_navigation))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral50)
            )
        },
        containerColor = KoinTheme.colors.neutral50
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KoinTheme.colors.primary500)
            }
        } else {
            StepCenteredBox(paddingValues) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(stringResource(Res.string.final_check_intro), fontSize = 16.sp, fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(stringResource(Res.string.field_category), uiState.selectedCategoryName)
                        InfoRow(stringResource(Res.string.field_store_name_label), uiState.name)
                        InfoRow(stringResource(Res.string.field_address), uiState.address)
                        InfoRow(stringResource(Res.string.field_phone), uiState.phone)
                        InfoRow(stringResource(Res.string.info_card_payment), if (uiState.isCardOk) stringResource(Res.string.available) else stringResource(Res.string.unavailable))
                        InfoRow(stringResource(Res.string.info_payment_bank), if (uiState.isBankOk) stringResource(Res.string.available) else stringResource(Res.string.unavailable))
                        InfoRow(stringResource(Res.string.info_delivery), if (uiState.isDeliveryOk) stringResource(Res.string.available) else stringResource(Res.string.unavailable))
                        if (uiState.isDeliveryOk && uiState.deliveryPrice.isNotEmpty()) {
                            InfoRow(stringResource(Res.string.info_delivery_price), "${uiState.deliveryPrice}원")
                        }
                        if (uiState.description.isNotEmpty()) {
                            InfoRow(stringResource(Res.string.field_description), uiState.description)
                        }
                    }
                    if (uiState.errorMessage.isNotEmpty()) {
                        KoinTextFieldAlert(message = uiState.errorMessage, type = KoinTextFieldAlertType.Error)
                    }
                    GradientActionButton(
                        text = stringResource(Res.string.insert_store_submit_btn),
                        onClick = onSubmit,
                        isLoading = uiState.isLoading,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

// --- Complete ---

@Composable
internal fun CompleteStep(
    onNavigateToStoreMain: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
        Column(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = KoinTheme.colors.primary500,
                modifier = Modifier.size(80.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = stringResource(Res.string.insert_store_complete_title),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = KoinTheme.colors.neutral800
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.insert_store_complete_desc),
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                color = KoinTheme.colors.neutral700
            )
            Spacer(modifier = Modifier.height(48.dp))
            GradientActionButton(
                text = stringResource(Res.string.go_to_my_store),
                onClick = onNavigateToStoreMain,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// --- Shared components ---

@Composable
private fun StepCenteredBox(
    paddingValues: PaddingValues,
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
        Box(
            modifier = Modifier
                .widthIn(max = 720.dp)
                .fillMaxHeight()
                .align(Alignment.TopCenter)
        ) {
            content()
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Medium,
        color = KoinTheme.colors.neutral700
    )
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = KoinTheme.colors.neutral700,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 14.sp,
            color = KoinTheme.colors.neutral800,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun InsertStoreCoverImageRow(
    coverImages: List<PlatformFile>,
    onAddImage: () -> Unit,
    onRemoveImage: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        coverImages.forEachIndexed { index, file ->
            Box(modifier = Modifier.size(90.dp).padding(top = 4.dp, end = 4.dp)) {
                Box(
                    modifier = Modifier
                        .size(86.dp)
                        .border(1.dp, KoinTheme.colors.neutral300, RoundedCornerShape(8.dp))
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
                        .clickable { onRemoveImage(index) },
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
        if (coverImages.size < 3) {
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
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) KoinTheme.colors.neutral200 else KoinTheme.colors.neutral100
    val contentColor = if (selected) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        if (selected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(16.dp)
            )
        }
        Text(label, fontSize = 14.sp, color = contentColor)
    }
}
