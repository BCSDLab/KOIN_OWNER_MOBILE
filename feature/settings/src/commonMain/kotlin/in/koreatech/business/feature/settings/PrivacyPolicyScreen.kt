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
import koreatech.business.designsystem.resources.privacy_policy
import org.jetbrains.compose.resources.stringResource

private data class PrivacySection(val heading: String, val body: String)

private val PRIVACY_SECTIONS = listOf(
    PrivacySection(
        "1. 수집하는 개인정보",
        "회원 가입 및 서비스 제공을 위해 필수적으로 수집하는 정보는 다음과 같습니다. 성명, 이메일, 연락처, 사업자 정보(상호명, 주소, 사업자 등록번호)."
    ),
    PrivacySection(
        "2. 개인정보의 이용 목적",
        "수집한 개인정보는 서비스 제공, 본인 확인, 결제 및 정산, 고객 문의 응대, 서비스 개선을 위한 통계 분석, 법령상 의무 이행 목적으로만 이용됩니다."
    ),
    PrivacySection(
        "3. 개인정보의 보유 및 이용 기간",
        "회원 탈퇴 시까지 보유하며, 전자상거래법 등 관계 법령에 따라 보존 의무가 있는 정보는 해당 기간 동안 별도 보관 후 파기합니다."
    ),
    PrivacySection(
        "4. 개인정보의 제3자 제공",
        "원칙적으로 이용자의 개인정보를 제3자에게 제공하지 않으며, 이용자의 동의가 있거나 법령에 의한 경우에만 제공합니다."
    ),
    PrivacySection(
        "5. 이용자의 권리",
        "이용자는 언제든지 본인의 개인정보를 조회, 수정, 삭제, 처리 정지를 요구할 수 있습니다. 고객센터로 문의해주세요."
    ),
    PrivacySection(
        "6. 문의처",
        "개인정보 관련 문의는 privacy@koin.im 으로 보내주세요. 영업일 기준 3일 이내에 답변드립니다."
    ),
)

@Composable
fun PrivacyPolicyScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = stringResource(Res.string.privacy_policy),
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
                    .background(KoinTheme.colors.primary100)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
            Spacer(Modifier.height(16.dp))
            Text(
                text = "BCSDLab(이하 \"회사\")은 이용자의 개인정보를 중요하게 생각하며, 「개인정보 보호법」 등 관련 법령을 준수하고 있습니다.",
                style = MaterialTheme.typography.bodyMedium,
                color = KoinTheme.colors.neutral800Variant,
                lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.4f
            )
            Spacer(Modifier.height(20.dp))
            PRIVACY_SECTIONS.forEach { section ->
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
