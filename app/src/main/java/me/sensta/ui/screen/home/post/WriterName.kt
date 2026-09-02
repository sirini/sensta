package me.sensta.ui.screen.home.post

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CameraAlt
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import me.domain.model.common.NuboWriter

@Composable
fun WriterName(
    writer: NuboWriter,
    style: TextStyle,
    color: Color = LocalContentColor.current
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = writer.name,
            style = style,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f, fill = false)
        )
        writer.badges.forEach { badge ->
            Icon(
                imageVector = if (badge.key == "sensta-app") Icons.Outlined.CameraAlt else Icons.Outlined.WorkspacePremium,
                contentDescription = "${badge.name}: ${badge.description}",
                tint = if (badge.key == "sensta-app") MaterialTheme.colorScheme.primary else color,
                modifier = Modifier.size(15.dp)
            )
        }
    }
}
