package me.sensta.ui.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import me.data.env.Env

@Composable
fun ChatAvatar(
    profile: String,
    name: String,
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainerHigh
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        if (profile.isBlank()) {
            DefaultChatAvatarIcon(name)
        } else {
            SubcomposeAsyncImage(
                model = Env.DOMAIN + profile,
                contentDescription = "${name}님의 프로필 이미지",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                loading = { DefaultChatAvatarIcon(name) },
                error = { DefaultChatAvatarIcon(name) }
            )
        }
    }
}

@Composable
private fun DefaultChatAvatarIcon(name: String) {
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = if (name.isBlank()) "기본 프로필" else "${name}님의 기본 프로필",
        tint = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = Modifier.fillMaxSize()
    )
}
