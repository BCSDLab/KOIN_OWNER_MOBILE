@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import `in`.koreatech.business.ui.component.KoinCard
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.back_navigation
import koreatech.business.designsystem.resources.oss_licenses
import koreatech.business.designsystem.resources.oss_search_placeholder
import org.jetbrains.compose.resources.stringResource

private data class OssLibrary(
    val name: String,
    val version: String,
    val license: String,
    val url: String
)

private val OSS_LIST = listOf(
    OssLibrary("Kotlin",               "2.1.0",   "Apache 2.0",   "kotlinlang.org"),
    OssLibrary("Compose Multiplatform","1.8.2",   "Apache 2.0",   "jetbrains.com/compose"),
    OssLibrary("Koin",                 "4.0.4",   "Apache 2.0",   "insert-koin.io"),
    OssLibrary("Ktor",                 "3.1.3",   "Apache 2.0",   "ktor.io"),
    OssLibrary("Orbit MVI",            "9.0.0",   "Apache 2.0",   "orbit-mvi.github.io"),
    OssLibrary("Kotlinx Serialization","1.8.1",   "Apache 2.0",   "github.com/Kotlin/kotlinx.serialization"),
    OssLibrary("Kotlinx Coroutines",   "1.10.2",  "Apache 2.0",   "github.com/Kotlin/kotlinx.coroutines"),
    OssLibrary("Coil",                 "3.1.0",   "Apache 2.0",   "coil-kt.github.io/coil"),
    OssLibrary("DataStore",            "1.1.7",   "Apache 2.0",   "developer.android.com"),
    OssLibrary("Encrypted DataStore",  "2.0.0",   "Apache 2.0",   "github.com/osipxd/encrypted-datastore"),
)

@Composable
fun OSSLicensesScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query) {
        if (query.isBlank()) OSS_LIST
        else OSS_LIST.filter { it.name.contains(query, ignoreCase = true) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .then(Modifier)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.oss_licenses),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral0)
        )

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            placeholder = {
                Text(
                    text = stringResource(Res.string.oss_search_placeholder),
                    color = KoinTheme.colors.neutral500
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = KoinTheme.colors.primary500,
                unfocusedBorderColor = KoinTheme.colors.neutral300,
                cursorColor = KoinTheme.colors.primary500
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Text(
            text = "KOIN 사장님 앱은 다음 오픈소스 라이브러리를 사용합니다. (${filtered.size}/${OSS_LIST.size})",
            style = MaterialTheme.typography.bodySmall,
            color = KoinTheme.colors.neutral500,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            item {
                KoinCard(modifier = Modifier.fillMaxWidth(), padding = PaddingValues(0.dp), spacing = 0.dp) {
                    filtered.forEachIndexed { index, lib ->
                        if (index > 0) {
                            HorizontalDivider(color = KoinTheme.colors.neutral200, modifier = Modifier.padding(horizontal = 16.dp))
                        }
                        OssRow(lib)
                    }
                }
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun OssRow(lib: OssLibrary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = lib.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = KoinTheme.colors.neutral800
                )
                Text(
                    text = "v${lib.version}",
                    fontSize = 11.sp,
                    color = KoinTheme.colors.neutral500
                )
            }
            Text(
                text = "${lib.license} · ${lib.url}",
                style = MaterialTheme.typography.bodySmall,
                color = KoinTheme.colors.neutral500,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = KoinTheme.colors.neutral500,
            modifier = Modifier.size(16.dp)
        )
    }
}
