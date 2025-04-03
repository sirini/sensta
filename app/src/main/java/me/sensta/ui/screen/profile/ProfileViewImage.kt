package me.sensta.ui.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import me.data.env.Env
import me.sensta.viewmodel.AuthViewModel

@Composable
fun ProfileViewImage() {
    val authViewModel: AuthViewModel = hiltViewModel()

    Box(
        modifier = Modifier
            .width(180.dp)
            .height(160.dp)
            .clickable {
                /* TODO */
            },
        contentAlignment = Alignment.Center
    ) {
        // 프로필 이미지 혹은 빈 아이콘 보여주기
        if (authViewModel.user.profile.isNotEmpty()) {
            AsyncImage(
                model = Env.domain + authViewModel.user.profile,
                contentDescription = authViewModel.user.name,
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )
        } else {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "user empty profile image",
                modifier = Modifier
                    .size(160.dp)
                    .clip(CircleShape)
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground)
                    .clickable {
                        /* TODO */
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}