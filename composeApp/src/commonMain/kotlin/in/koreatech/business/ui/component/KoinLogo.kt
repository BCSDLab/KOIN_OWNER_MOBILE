package `in`.koreatech.business.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import koin_owner_mobile.composeapp.generated.resources.Res
import koin_owner_mobile.composeapp.generated.resources.ic_logo_coin
import koin_owner_mobile.composeapp.generated.resources.ic_logo_coin_color
import org.jetbrains.compose.resources.painterResource

@Composable
fun KoinLogo(
    modifier: Modifier = Modifier.size(64.dp),
    colored: Boolean = false
) {
    Image(
        painter = painterResource(
            if (colored) Res.drawable.ic_logo_coin_color else Res.drawable.ic_logo_coin
        ),
        contentDescription = "KOIN 로고",
        modifier = modifier
    )
}
