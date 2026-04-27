package `in`.koreatech.business.feature.store.menu.categories

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.domain.model.MenuCategory
import `in`.koreatech.business.ui.component.BusinessSnackbarHost
import `in`.koreatech.business.ui.component.DesktopTopBar
import `in`.koreatech.business.ui.component.GlobalLoadingOverlay
import `in`.koreatech.business.ui.component.KoinButton
import `in`.koreatech.business.ui.component.StoreSidebarActions
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.*
import koreatech.business.designsystem.resources.Res
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.orbitmvi.orbit.compose.collectAsState

@Composable
fun ManageCategoriesScreen(
    storeId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    sidebarActions: StoreSidebarActions = StoreSidebarActions(),
    viewModel: ManageCategoriesViewModel = koinViewModel()
) {
    val uiState by viewModel.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var editingCategoryId by remember { mutableStateOf<Int?>(null) }
    var editingDraft by remember { mutableStateOf("") }
    var showAddRow by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }

    LaunchedEffect(storeId) { viewModel.load(storeId) }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotEmpty()) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
            viewModel.clearError()
        }
    }

    uiState.blockDeleteCategory?.let { blocked ->
        AlertDialog(
            onDismissRequest = viewModel::clearBlockDelete,
            title = { Text(stringResource(Res.string.cannot_delete_title)) },
            text = {
                Text(stringResource(Res.string.cannot_delete_category_desc, blocked.name, blocked.menus.size))
            },
            confirmButton = {
                TextButton(onClick = viewModel::clearBlockDelete) { Text(stringResource(Res.string.confirm)) }
            }
        )
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().background(KoinTheme.colors.neutral50)) {
        val isDesktop = maxWidth >= 440.dp
        if (isDesktop) {
            Column(modifier = Modifier.fillMaxSize()) {
                DesktopTopBar(
                    title = stringResource(Res.string.manage_categories_title),
                    subtitle = stringResource(Res.string.manage_categories_subtitle_desktop),
                    actions = {
                        if (!showAddRow) {
                            KoinButton(
                                text = stringResource(Res.string.add_category_plus),
                                onClick = { showAddRow = true }
                            )
                        }
                    }
                )

                BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    val sidePadding = (this.maxWidth * 0.04f).coerceIn(16.dp, 80.dp)
                    Column(
                        modifier = Modifier
                            .widthIn(max = 720.dp)
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = sidePadding, vertical = 24.dp)
                    ) {
                        CategoryListCard(
                            categories = uiState.categories,
                            editingCategoryId = editingCategoryId,
                            editingDraft = editingDraft,
                            onEditDraftChange = { editingDraft = it },
                            onStartEdit = { cat ->
                                editingCategoryId = cat.id
                                editingDraft = cat.name
                            },
                            onCommitEdit = { cat ->
                                if (editingDraft.isNotBlank()) {
                                    viewModel.renameCategory(cat.id, editingDraft)
                                }
                                editingCategoryId = null
                            },
                            onCancelEdit = { editingCategoryId = null },
                            onDelete = { cat -> viewModel.deleteCategory(cat.id) },
                            showAddRow = showAddRow,
                            newCategoryName = newCategoryName,
                            onNewCategoryNameChange = { newCategoryName = it },
                            onAddSubmit = {
                                viewModel.addCategory(newCategoryName)
                                newCategoryName = ""
                                showAddRow = false
                            },
                            onAddCancel = {
                                showAddRow = false
                                newCategoryName = ""
                            },
                            onShowAddRow = { showAddRow = true }
                        )
                    }
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                // Mobile top bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(KoinTheme.colors.neutral0)
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
                        text = stringResource(Res.string.manage_categories_title),
                        style = MaterialTheme.typography.headlineMedium,
                        color = KoinTheme.colors.neutral800,
                        modifier = Modifier.weight(1f)
                    )
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(0.dp)
                ) {
                    item {
                        Text(
                            text = stringResource(Res.string.manage_categories_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = KoinTheme.colors.neutral500,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                    }

                    item {
                        CategoryListCard(
                            categories = uiState.categories,
                            editingCategoryId = editingCategoryId,
                            editingDraft = editingDraft,
                            onEditDraftChange = { editingDraft = it },
                            onStartEdit = { cat ->
                                editingCategoryId = cat.id
                                editingDraft = cat.name
                            },
                            onCommitEdit = { cat ->
                                if (editingDraft.isNotBlank()) {
                                    viewModel.renameCategory(cat.id, editingDraft)
                                }
                                editingCategoryId = null
                            },
                            onCancelEdit = { editingCategoryId = null },
                            onDelete = { cat -> viewModel.deleteCategory(cat.id) },
                            showAddRow = showAddRow,
                            newCategoryName = newCategoryName,
                            onNewCategoryNameChange = { newCategoryName = it },
                            onAddSubmit = {
                                viewModel.addCategory(newCategoryName)
                                newCategoryName = ""
                                showAddRow = false
                            },
                            onAddCancel = {
                                showAddRow = false
                                newCategoryName = ""
                            },
                            onShowAddRow = { showAddRow = true }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(32.dp)) }
                }
            }
        }

        BusinessSnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
        GlobalLoadingOverlay(visible = uiState.isLoading)
    }
}

@Composable
private fun CategoryListCard(
    categories: List<MenuCategory>,
    editingCategoryId: Int?,
    editingDraft: String,
    onEditDraftChange: (String) -> Unit,
    onStartEdit: (MenuCategory) -> Unit,
    onCommitEdit: (MenuCategory) -> Unit,
    onCancelEdit: () -> Unit,
    onDelete: (MenuCategory) -> Unit,
    showAddRow: Boolean,
    newCategoryName: String,
    onNewCategoryNameChange: (String) -> Unit,
    onAddSubmit: () -> Unit,
    onAddCancel: () -> Unit,
    onShowAddRow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(KoinTheme.colors.neutral0)
    ) {
        categories.forEachIndexed { idx, cat ->
            if (idx > 0) {
                HorizontalDivider(color = KoinTheme.colors.neutral300)
            }
            CategoryRow(
                category = cat,
                isEditing = editingCategoryId == cat.id,
                editDraft = editingDraft,
                onEditDraftChange = onEditDraftChange,
                onStartEdit = { onStartEdit(cat) },
                onCommitEdit = { onCommitEdit(cat) },
                onCancelEdit = onCancelEdit,
                onDelete = { onDelete(cat) }
            )
        }

        if (categories.isNotEmpty() || showAddRow) {
            HorizontalDivider(color = KoinTheme.colors.neutral300)
        }

        if (showAddRow) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(KoinTheme.colors.primary100.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = onNewCategoryNameChange,
                    placeholder = {
                        Text(
                            stringResource(Res.string.ph_category_name),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = KoinTheme.colors.primary500,
                        unfocusedBorderColor = KoinTheme.colors.neutral300
                    )
                )
                TextButton(
                    onClick = onAddSubmit,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = KoinTheme.colors.primary500
                    )
                ) { Text(stringResource(Res.string.add_category_inline)) }
                TextButton(
                    onClick = onAddCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = KoinTheme.colors.neutral500
                    )
                ) { Text(stringResource(Res.string.cancel)) }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onShowAddRow)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = KoinTheme.colors.primary500,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = stringResource(Res.string.add_category_inline),
                    style = MaterialTheme.typography.bodyMedium,
                    color = KoinTheme.colors.primary500
                )
            }
        }
    }
}

@Composable
private fun CategoryRow(
    category: MenuCategory,
    isEditing: Boolean,
    editDraft: String,
    onEditDraftChange: (String) -> Unit,
    onStartEdit: () -> Unit,
    onCommitEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        if (isEditing) {
            OutlinedTextField(
                value = editDraft,
                onValueChange = onEditDraftChange,
                singleLine = true,
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = KoinTheme.colors.primary500,
                    unfocusedBorderColor = KoinTheme.colors.neutral300
                )
            )
            IconButton(
                onClick = onCommitEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = stringResource(Res.string.confirm),
                    tint = KoinTheme.colors.primary500,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onCancelEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.cancel),
                    tint = KoinTheme.colors.neutral500,
                    modifier = Modifier.size(18.dp)
                )
            }
        } else {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.bodyLarge,
                    color = KoinTheme.colors.neutral800
                )
                Text(
                    text = if (category.menus.isEmpty()) stringResource(Res.string.no_menu_in_category) else stringResource(Res.string.menu_count, category.menus.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = KoinTheme.colors.neutral500
                )
            }
            IconButton(
                onClick = onStartEdit,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(Res.string.edit),
                    tint = KoinTheme.colors.neutral500,
                    modifier = Modifier.size(18.dp)
                )
            }
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete),
                    tint = KoinTheme.colors.danger600,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
