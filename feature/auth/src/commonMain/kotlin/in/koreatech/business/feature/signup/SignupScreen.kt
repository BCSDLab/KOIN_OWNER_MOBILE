@file:OptIn(androidx.compose.ui.ExperimentalComposeUiApi::class)

package `in`.koreatech.business.feature.signup

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.domain.model.signup.ShopSearchResult
import `in`.koreatech.business.platform.PlatformFile
import `in`.koreatech.business.platform.rememberFilePicker
import `in`.koreatech.business.ui.component.BusinessNumberVisualTransformation
import `in`.koreatech.business.ui.component.FilledActionButton
import `in`.koreatech.business.ui.component.KoinProgressHeader
import `in`.koreatech.business.ui.component.KoinTextField
import `in`.koreatech.business.ui.component.KoinTextFieldAlert
import `in`.koreatech.business.ui.component.KoinTextFieldAlertType
import `in`.koreatech.business.ui.component.PhoneVisualTransformation
import `in`.koreatech.business.ui.component.ShopPhoneVisualTransformation
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource

// ──────────────────────────────────────────────
// Shared scaffold for signup steps
// ──────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SignupStepScaffold(
    currentStep: Int,
    title: String,
    subtitle: String? = null,
    onBack: () -> Unit,
    scrollable: Boolean = false,
    modifier: Modifier = Modifier,
    bottomContent: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold(
        modifier = modifier,
        containerColor = KoinTheme.colors.neutral50,
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.back_navigation),
                            tint = KoinTheme.colors.neutral800
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = KoinTheme.colors.neutral50
                )
            )
        },
        bottomBar = {
            if (bottomContent != null) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 480.dp)
                            .align(Alignment.Center)
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 24.dp, vertical = 16.dp)
                    ) {
                        bottomContent()
                    }
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(paddingValues)) {
            Column(
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(horizontal = 24.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                KoinProgressHeader(
                    currentStep = currentStep,
                    totalSteps = 7,
                    title = title,
                    subtitle = subtitle
                )
                content()
            }
        }
    }
}

// ──────────────────────────────────────────────
// Step 1: Terms
// ──────────────────────────────────────────────

@Composable
internal fun TermsStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onToggleAll: () -> Unit,
    onToggleTerm: (String) -> Unit,
    onToggleExpand: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 1,
        title = stringResource(Res.string.terms_title),
        onBack = onBack,
        scrollable = true,
        modifier = modifier,
        bottomContent = {
            FilledActionButton(
                text = stringResource(Res.string.next),
                onClick = onNext,
                enabled = uiState.requiredTermsAgreed
            )
        }
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        // All agree row
        val allAgreed = uiState.allTermsAgreed
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (allAgreed) {
                        KoinTheme.colors.primary500.copy(alpha = 0.08f)
                    } else {
                        KoinTheme.colors.neutral100
                    }
                )
                .border(
                    width = 1.dp,
                    color = if (allAgreed) KoinTheme.colors.primary500 else KoinTheme.colors.neutral400,
                    shape = RoundedCornerShape(12.dp)
                )
                .clickable(onClick = onToggleAll)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (allAgreed) Icons.Filled.CheckCircle else Icons.Outlined.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = if (allAgreed) KoinTheme.colors.primary500 else KoinTheme.colors.neutral500
            )
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = stringResource(Res.string.terms_agree_all),
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = if (allAgreed) KoinTheme.colors.primary500 else KoinTheme.colors.neutral800
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = KoinTheme.colors.neutral400)
        Spacer(modifier = Modifier.height(8.dp))

        // Individual terms
        uiState.terms.forEachIndexed { index, term ->
            TermItemRow(
                term = term,
                onToggle = { onToggleTerm(term.id) },
                onToggleExpand = { onToggleExpand(term.id) }
            )
            if (index < uiState.terms.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun TermItemRow(
    term: TermItem,
    onToggle: () -> Unit,
    onToggleExpand: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onToggle)
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = term.isAgreed,
                onCheckedChange = { onToggle() },
                colors = CheckboxDefaults.colors(
                    checkedColor = KoinTheme.colors.primary500,
                    uncheckedColor = KoinTheme.colors.neutral400,
                    checkmarkColor = KoinTheme.colors.neutral50
                )
            )
            Text(
                text = term.title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = KoinTheme.colors.neutral800,
                modifier = Modifier.weight(1f)
            )
            val arrowRotation by animateFloatAsState(
                targetValue = if (term.isExpanded) 180f else 0f,
                label = "arrowRotation"
            )
            IconButton(onClick = onToggleExpand, modifier = Modifier.size(32.dp)) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = if (term.isExpanded) stringResource(Res.string.collapse) else stringResource(Res.string.expand),
                    tint = KoinTheme.colors.neutral500,
                    modifier = Modifier.rotate(arrowRotation)
                )
            }
        }
        AnimatedVisibility(
            visible = term.isExpanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KoinTheme.colors.neutral100)
                        .padding(14.dp),
                    contentAlignment = Alignment.TopStart
                ) {
                    if (term.content.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center).size(24.dp),
                            color = KoinTheme.colors.primary500,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Box(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                            Text(
                                text = term.content,
                                fontSize = 12.sp,
                                color = KoinTheme.colors.neutral700,
                                lineHeight = 19.2.sp
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

// ──────────────────────────────────────────────
// Step 2: Phone Setup
// ──────────────────────────────────────────────

@Composable
internal fun AccountSetupStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onPhoneChanged: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 2,
        title = stringResource(Res.string.phone_verify_title),
        subtitle = stringResource(Res.string.phone_verify_subtitle),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = stringResource(Res.string.field_phone),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = KoinTheme.colors.neutral800
        )
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.phoneNumber,
            onValueChange = onPhoneChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "010-0000-0000",
            visualTransformation = PhoneVisualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done)
        )
        if (uiState.phoneError.isNotEmpty()) {
            KoinTextFieldAlert(message = uiState.phoneError, type = KoinTextFieldAlertType.Error)
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledActionButton(
            text = stringResource(Res.string.request_sms),
            onClick = onNext,
            isLoading = uiState.isLoading,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────
// Step 3: SMS Verify
// ──────────────────────────────────────────────

@Composable
internal fun SmsVerifyStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onCodeChanged: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 3,
        title = stringResource(Res.string.sms_verify_title),
        subtitle = stringResource(Res.string.sms_sent_to_phone_signup, uiState.phoneNumber),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(
            text = stringResource(Res.string.field_sms_code),
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = KoinTheme.colors.neutral800
        )
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.smsCode,
            onValueChange = onCodeChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_sms_code),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )
        if (uiState.smsError.isNotEmpty()) {
            KoinTextFieldAlert(message = uiState.smsError, type = KoinTextFieldAlertType.Error)
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledActionButton(
            text = stringResource(Res.string.confirm),
            onClick = onNext,
            isLoading = uiState.isLoading,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────
// Step 4: Enter Password
// ──────────────────────────────────────────────

@Composable
internal fun EnterPasswordStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onNameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onPasswordConfirmChanged: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onTogglePasswordConfirmVisibility: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 4,
        title = stringResource(Res.string.basic_info_title),
        subtitle = stringResource(Res.string.basic_info_subtitle),
        onBack = onBack,
        scrollable = true,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(text = stringResource(Res.string.field_name), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.name,
            onValueChange = onNameChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_name),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = stringResource(Res.string.field_password), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.password,
            onValueChange = onPasswordChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_password),
            visualTransformation = if (uiState.isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = KoinTheme.colors.neutral500
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Next)
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = stringResource(Res.string.field_password_confirm), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.passwordConfirm,
            onValueChange = onPasswordConfirmChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_password_confirm),
            visualTransformation = if (uiState.isPasswordConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = onTogglePasswordConfirmVisibility) {
                    Icon(
                        imageVector = if (uiState.isPasswordConfirmVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = KoinTheme.colors.neutral500
                    )
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done)
        )

        if (uiState.passwordError.isNotEmpty()) {
            KoinTextFieldAlert(message = uiState.passwordError, type = KoinTextFieldAlertType.Error)
        }

        Spacer(modifier = Modifier.height(40.dp))

        FilledActionButton(text = stringResource(Res.string.next), onClick = onNext)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────
// Step 5: Business Number
// ──────────────────────────────────────────────

@Composable
internal fun BusinessNumberStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onBusinessNumberChanged: (String) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 5,
        title = stringResource(Res.string.business_number_title),
        subtitle = stringResource(Res.string.business_number_subtitle),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(text = stringResource(Res.string.field_business_number), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.businessNumber,
            onValueChange = onBusinessNumberChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "000-00-00000",
            visualTransformation = BusinessNumberVisualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.business_number_hint),
            fontSize = 12.sp,
            color = KoinTheme.colors.neutral500
        )
        if (uiState.businessNumberError.isNotEmpty()) {
            KoinTextFieldAlert(message = uiState.businessNumberError, type = KoinTextFieldAlertType.Error)
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledActionButton(text = stringResource(Res.string.next), onClick = onNext)

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────
// Step 6: Store Name
// ──────────────────────────────────────────────

@Composable
internal fun StoreNameStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onStoreNameChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onEnterManually: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 6,
        title = stringResource(Res.string.store_search_title),
        subtitle = stringResource(Res.string.store_search_subtitle),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(text = stringResource(Res.string.field_store_name), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.storeName,
            onValueChange = onStoreNameChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(Res.string.ph_store_name),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = stringResource(Res.string.store_search_hint),
            fontSize = 12.sp,
            color = KoinTheme.colors.neutral500
        )
        if (uiState.storeNameError.isNotEmpty()) {
            KoinTextFieldAlert(message = uiState.storeNameError, type = KoinTextFieldAlertType.Error)
        }

        Spacer(modifier = Modifier.weight(1f))

        FilledActionButton(
            text = stringResource(Res.string.search),
            onClick = onSearch,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(12.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, KoinTheme.colors.primary500, RoundedCornerShape(12.dp))
                .clickable(enabled = !uiState.isLoading, onClick = onEnterManually),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.store_direct_input),
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = KoinTheme.colors.primary500
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
internal fun SearchStoreStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onSelectShop: (ShopSearchResult) -> Unit,
    onEnterManually: () -> Unit,
    modifier: Modifier = Modifier
) {
    SignupStepScaffold(
        currentStep = 6,
        title = stringResource(Res.string.store_search_title),
        subtitle = stringResource(Res.string.store_search_results_subtitle),
        onBack = onBack,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = KoinTheme.colors.primary500, modifier = Modifier.size(32.dp))
            }
        } else if (uiState.searchResults.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.search_results_count, uiState.searchResults.size),
                fontSize = 13.sp,
                color = KoinTheme.colors.neutral500
            )
            Spacer(modifier = Modifier.height(12.dp))
            uiState.searchResults.forEach { shop ->
                ShopResultItem(shop = shop, onClick = { onSelectShop(shop) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        } else {
            Text(
                text = stringResource(Res.string.store_search_no_results),
                fontSize = 14.sp,
                color = KoinTheme.colors.neutral700
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, KoinTheme.colors.neutral400, RoundedCornerShape(12.dp))
                .clickable(enabled = !uiState.isLoading, onClick = onEnterManually),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(Res.string.store_direct_input),
                fontSize = 16.sp,
                fontWeight = FontWeight.W600,
                color = KoinTheme.colors.primary500
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun ShopResultItem(
    shop: ShopSearchResult,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(KoinTheme.colors.neutral100)
            .border(1.dp, KoinTheme.colors.neutral400, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = shop.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium,
                color = KoinTheme.colors.neutral800
            )
            if (shop.address.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = shop.address,
                    fontSize = 12.sp,
                    color = KoinTheme.colors.neutral500
                )
            }
        }
    }
}

// ──────────────────────────────────────────────
// Step 7: Attach File
// ──────────────────────────────────────────────

@Composable
internal fun AttachFileStep(
    uiState: SignupUiState,
    onBack: () -> Unit,
    onShopPhoneChanged: (String) -> Unit,
    onAddFile: (PlatformFile) -> Unit,
    onRemoveFile: (PlatformFile) -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pickFile = rememberFilePicker(onFilePicked = onAddFile)
    SignupStepScaffold(
        currentStep = 7,
        title = stringResource(Res.string.attach_files_title),
        subtitle = stringResource(Res.string.attach_files_subtitle),
        onBack = onBack,
        scrollable = true,
        modifier = modifier
    ) {
        Spacer(modifier = Modifier.height(64.dp))

        Text(text = stringResource(Res.string.field_store_phone), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(4.dp))
        KoinTextField(
            value = uiState.shopPhoneNumber,
            onValueChange = onShopPhoneChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = "041-000-0000",
            visualTransformation = ShopPhoneVisualTransformation,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = stringResource(Res.string.field_attached_files), fontSize = 16.sp, fontWeight = FontWeight.Medium, color = KoinTheme.colors.neutral800)
        Spacer(modifier = Modifier.height(8.dp))

        uiState.attachedFiles.forEach { file ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, KoinTheme.colors.neutral200, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = file.name,
                    fontSize = 14.sp,
                    color = KoinTheme.colors.neutral800,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { onRemoveFile(file) },
                    modifier = Modifier.size(20.dp)
                ) {
                    Text("✕", fontSize = 12.sp, color = KoinTheme.colors.neutral600)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        if (uiState.attachedFiles.size < 5) {
            Surface(
                onClick = pickFile,
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp),
                color = KoinTheme.colors.neutral50,
                border = androidx.compose.foundation.BorderStroke(1.dp, KoinTheme.colors.neutral400)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(Res.string.add_file),
                        fontSize = 14.sp,
                        color = KoinTheme.colors.primary500,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (uiState.attachFileError.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            KoinTextFieldAlert(message = uiState.attachFileError, type = KoinTextFieldAlertType.Error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        FilledActionButton(
            text = stringResource(Res.string.sign_up_button),
            onClick = onNext,
            isLoading = uiState.isLoading,
            enabled = !uiState.isLoading
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

// ──────────────────────────────────────────────
// Complete
// ──────────────────────────────────────────────

@Composable
internal fun SignupCompleteStep(
    onGoToSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KoinTheme.colors.neutral50)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = KoinTheme.colors.success700
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = stringResource(Res.string.signup_complete_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = KoinTheme.colors.neutral800,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(Res.string.signup_complete_desc),
            fontSize = 14.sp,
            color = KoinTheme.colors.neutral500,
            textAlign = TextAlign.Center,
            lineHeight = 19.6.sp
        )

        Spacer(modifier = Modifier.height(48.dp))

        FilledActionButton(
            text = stringResource(Res.string.go_to_login),
            onClick = onGoToSignIn,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
