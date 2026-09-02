package me.sensta.ui.common

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import me.domain.model.common.NuboBadge

@Composable
fun AchievementCelebrationDialog(
    badge: NuboBadge,
    remaining: Int,
    onConfirm: () -> Unit,
    onViewProfile: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val reveal = remember(badge.key) { Animatable(0.35f) }
    val confetti = remember(badge.key) { Animatable(0f) }
    val colors = listOf(
        MaterialTheme.colorScheme.primary,
        MaterialTheme.colorScheme.tertiary,
        Color(0xFFF59E0B),
        Color(0xFF38BDF8),
        Color(0xFFFB7185)
    )

    LaunchedEffect(badge.key) {
        reveal.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy))
    }
    LaunchedEffect(badge.key) {
        confetti.animateTo(1f, tween(1_250))
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
                .graphicsLayer { alpha = reveal.value }
                .scale(reveal.value),
            shape = RoundedCornerShape(28.dp)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    repeat(18) { index ->
                        val startX = ((index * 43) % 100) / 100f * size.width
                        val travel = size.height * 0.72f
                        val y = -18f + confetti.value * travel + (index % 4) * 17f
                        drawCircle(
                            color = colors[index % colors.size].copy(alpha = 1f - confetti.value * 0.72f),
                            radius = 5f + (index % 3) * 1.5f,
                            center = androidx.compose.ui.geometry.Offset(startX, y)
                        )
                    }
                }

                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(94.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = achievementIcon(badge.iconKey),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(46.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))
                    Text(
                        "새로운 업적",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        badge.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                    Text(
                        badge.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                    if (remaining > 0) {
                        Text(
                            "새 업적이 ${remaining}개 더 있습니다.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedButton(onClick = onViewProfile) { Text("내 업적 진열장") }
                        Button(onClick = onConfirm) { Text("확인") }
                    }
                }
            }
        }
    }
}
