package me.sensta.ui.screen.view.content

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.photo.NuboImage
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun ViewPostExifDescription(images: List<NuboImage>) {
    val commonViewModel = LocalCommonViewModel.current
    val image = images[commonViewModel.pagerIndex.intValue]

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.extraSmall,
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceContainer)
    ) {
        if (image.exif.model.isNotEmpty()) {
            Text(
                text = "${image.exif.make} ${image.exif.model} / ${image.exif.focalLength}mm / f${image.exif.aperture / 100f} / ${image.exif.exposure / 1000f}ms / ISO ${image.exif.iso}",
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            )
        }

        if (image.description.isNotEmpty()) {
            Text(
                text = image.description,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
