package `in`.koreatech.business.feature.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import `in`.koreatech.business.feature.settings.utils.TermCategory
import `in`.koreatech.business.feature.settings.utils.termToPolicySection

@Composable
fun ServiceTermsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val term by remember { mutableStateOf(termToPolicySection(TermCategory.KOIN)) }

    PolicyDocumentScreen(
        title = term.first,
        sections = term.second,
        onNavigateBack = onNavigateBack,
        modifier = modifier
    )
}
