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
import koin_owner_mobile.composeapp.generated.resources.Res
import koin_owner_mobile.composeapp.generated.resources.back_navigation
import koin_owner_mobile.composeapp.generated.resources.service_terms
import org.jetbrains.compose.resources.stringResource

private data class TermsSection(val heading: String, val body: String)

private val TERMS_SECTIONS = listOf(
    TermsSection(
        "제1조 (목적)",
        "본 약관은 BCSDLab이 제공하는 KOIN 사장님 서비스의 이용 조건 및 절차, 회사와 회원 간의 권리·의무 및 기타 필요한 사항을 규정함을 목적으로 합니다."
    ),
    TermsSection(
        "제2조 (정의)",
        "\"서비스\"란 회사가 제공하는 매장 관리, 메뉴 관리, 이벤트 관리 등 모든 기능을 말합니다. \"회원\"이란 본 약관에 동의하고 서비스를 이용하는 사업자를 말합니다."
    ),
    TermsSection(
        "제3조 (약관의 효력)",
        "본 약관은 서비스 화면에 게시하거나 기타의 방법으로 회원에게 공지함으로써 효력이 발생합니다. 회사는 필요한 경우 약관을 변경할 수 있으며, 변경된 약관은 공지 후 7일이 경과한 날부터 효력이 발생합니다."
    ),
    TermsSection(
        "제4조 (회원 가입)",
        "서비스를 이용하고자 하는 자는 회사가 정한 절차에 따라 회원 가입을 신청하고, 회사가 이를 승낙함으로써 회원이 됩니다."
    ),
    TermsSection(
        "제5조 (서비스 이용)",
        "회원은 본 약관 및 관련 법령에 따라 서비스를 이용하여야 합니다. 회원은 타인의 권리를 침해하거나 서비스 운영을 방해하는 행위를 하여서는 안 됩니다."
    ),
    TermsSection(
        "제6조 (책임의 제한)",
        "회사는 천재지변, 불가항력적 사유, 회원의 귀책 사유로 인한 손해에 대하여 책임을 지지 않습니다."
    ),
)

@Composable
fun ServiceTermsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.service_terms),
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

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(KoinTheme.colors.neutral50)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Text(
                text = "최종 개정일: 2026년 3월 15일 · 시행일: 2026년 4월 1일",
                style = MaterialTheme.typography.bodySmall,
                color = KoinTheme.colors.neutral500,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(KoinTheme.colors.neutral200)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
            Spacer(Modifier.height(20.dp))
            TERMS_SECTIONS.forEach { section ->
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
