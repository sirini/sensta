package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.photo.TsboardImage
import me.sensta.viewmodel.common.LocalCommonViewModel

@Composable
fun ViewPostAIDescription(images: List<TsboardImage>) {
    val commonViewModel = LocalCommonViewModel.current

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.2f))
    ) {
        Text(
            text = images[commonViewModel.pagerIndex.intValue].description,
            modifier = Modifier.padding(12.dp),
            style = MaterialTheme.typography.bodySmall
        )
    }
}