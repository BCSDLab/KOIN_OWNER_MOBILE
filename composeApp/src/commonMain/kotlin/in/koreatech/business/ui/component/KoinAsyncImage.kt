package `in`.koreatech.business.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrokenImage
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImagePainter
import coil3.compose.SubcomposeAsyncImage
import coil3.compose.SubcomposeAsyncImageContent
import `in`.koreatech.business.ui.theme.KoinTheme

@Composable
fun KoinAsyncImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 8.dp,
    contentScale: ContentScale = ContentScale.Crop
) {
    val shape = RoundedCornerShape(cornerRadius)
    SubcomposeAsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier.clip(shape),
        contentScale = contentScale
    ) {
        val state by painter.state.collectAsState()
        when (state) {
            is AsyncImagePainter.State.Loading -> KoinImagePlaceholder(loading = true)
            is AsyncImagePainter.State.Error -> KoinImagePlaceholder(loading = false, broken = true)
            AsyncImagePainter.State.Empty -> KoinImagePlaceholder(loading = false)
            is AsyncImagePainter.State.Success -> SubcomposeAsyncImageContent()
        }
    }
}

@Composable
private fun KoinImagePlaceholder(
    loading: Boolean,
    broken: Boolean = false,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(KoinTheme.colors.neutral100),
        contentAlignment = Alignment.Center
    ) {
        when {
            loading -> CircularProgressIndicator(
                color = KoinTheme.colors.primary500,
                modifier = Modifier.size(20.dp),
                strokeWidth = 2.dp
            )
            broken -> Icon(
                imageVector = Icons.Default.BrokenImage,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(24.dp)
            )
            else -> Icon(
                imageVector = Icons.Default.Image,
                contentDescription = null,
                tint = KoinTheme.colors.neutral500,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
