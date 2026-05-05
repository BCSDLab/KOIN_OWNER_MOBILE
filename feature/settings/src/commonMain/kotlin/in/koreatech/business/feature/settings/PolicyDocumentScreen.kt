@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package `in`.koreatech.business.feature.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import `in`.koreatech.business.ui.theme.KoinTheme
import koreatech.business.designsystem.resources.Res
import koreatech.business.designsystem.resources.back_navigation
import org.jetbrains.compose.resources.stringResource

data class PolicySection(val heading: String, val body: String)

@Composable
internal fun PolicyDocumentScreen(
    title: String,
    revisionLine: String? = null,
    intro: String? = null,
    sections: List<PolicySection>,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = title,
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
            colors = TopAppBarDefaults.topAppBarColors(containerColor = KoinTheme.colors.neutral50)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KoinTheme.colors.neutral50)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            revisionLine?.let {
                Text(
                    text = revisionLine,
                    style = MaterialTheme.typography.bodySmall,
                    color = KoinTheme.colors.neutral500,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(KoinTheme.colors.primary100)
                        .padding(horizontal = 12.dp, vertical = 10.dp)
                )
                Spacer(Modifier.height(16.dp))
            }
            intro?.let {
                Text(
                    text = intro,
                    style = MaterialTheme.typography.bodyMedium,
                    color = KoinTheme.colors.neutral800Variant,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f
                )
                Spacer(Modifier.height(20.dp))
            }
            sections.forEach { section ->
                Text(
                    text = section.heading,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = KoinTheme.colors.neutral800
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = section.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = KoinTheme.colors.neutral800Variant,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.5f
                )
                Spacer(Modifier.height(20.dp))
            }
        }
    }
}
