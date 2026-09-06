package me.sensta.ui.screen.home.post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sensta.wallpaper.WallpaperTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WallpaperConfirmationDialog(
    applyingTarget: WallpaperTarget?,
    onDismissRequest: () -> Unit,
    onApply: (WallpaperTarget) -> Unit
) {
    BasicAlertDialog(onDismissRequest = onDismissRequest) {
        Surface(
            modifier = Modifier
                .widthIn(min = 280.dp, max = 360.dp)
                .heightIn(max = 560.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "어디에 설정할까요?",
                    style = MaterialTheme.typography.headlineSmall
                )
                Spacer(modifier = Modifier.height(20.dp))

                WallpaperActionButton(
                    label = "홈 화면으로 설정",
                    target = WallpaperTarget.HOME_SCREEN,
                    applyingTarget = applyingTarget,
                    onApply = onApply
                )
                Spacer(modifier = Modifier.height(10.dp))
                WallpaperActionButton(
                    label = "홈 및 잠금 화면으로 설정",
                    target = WallpaperTarget.BOTH,
                    applyingTarget = applyingTarget,
                    onApply = onApply
                )
                Spacer(modifier = Modifier.height(10.dp))
                WallpaperActionButton(
                    label = "잠금 화면으로 설정",
                    target = WallpaperTarget.LOCK_SCREEN,
                    applyingTarget = applyingTarget,
                    onApply = onApply
                )
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    enabled = applyingTarget == null,
                    onClick = onDismissRequest,
                    shape = CircleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 52.dp)
                ) {
                    Text("설정하지 않음")
                }
            }
        }
    }
}

@Composable
private fun WallpaperActionButton(
    label: String,
    target: WallpaperTarget,
    applyingTarget: WallpaperTarget?,
    onApply: (WallpaperTarget) -> Unit
) {
    val isApplying = applyingTarget == target

    FilledTonalButton(
        enabled = applyingTarget == null,
        onClick = { onApply(target) },
        shape = CircleShape,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 52.dp)
    ) {
        if (isApplying) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Text(if (isApplying) "설정 중…" else label)
    }
}
