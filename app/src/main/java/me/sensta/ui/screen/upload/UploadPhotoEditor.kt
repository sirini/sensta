package me.sensta.ui.screen.upload

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import me.sensta.editor.EditablePhoto
import me.sensta.editor.PhotoFilter
import me.sensta.editor.PhotoFilterMatrix
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalUploadViewModel
import me.sensta.viewmodel.state.UploadState

@Composable
fun UploadPhotoEditor(onCrop: (Int, Uri) -> Unit) {
    val context = LocalContext.current
    val viewModel = LocalUploadViewModel.current
    val editor = viewModel.photoEditor
    val photos by editor.photos
    val selectedIndex by editor.selectedPhotoIndex
    val photo = photos.getOrNull(selectedIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "EDIT YOUR PHOTOS",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            fontFamily = robotoSlabFontFamily
        )
        Text(
            text = "${selectedIndex + 1} / ${photos.size}",
            modifier = Modifier.align(Alignment.CenterHorizontally),
            style = MaterialTheme.typography.labelMedium
        )
        Spacer(Modifier.height(10.dp))
        EditorPreview(photo)
        Spacer(Modifier.height(10.dp))
        PhotoSelector(photos, selectedIndex, editor::select)
        EditActions(
            onCrop = { onCrop(selectedIndex, photo.originalUri) },
            onRotate = editor::rotate,
            onMirror = editor::mirror,
            onReset = editor::reset
        )
        FilterSelector(photo, editor::setFilter)
        FilterIntensity(photo, editor::setIntensity)
        UploadBottomRow(
            onBack = { viewModel.setUploadState(UploadState.SelectImage) },
            onNext = { viewModel.finishEditing(context) }
        )
    }
}

@Composable
private fun EditorPreview(photo: EditablePhoto) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clip(RoundedCornerShape(6.dp))
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = photo.previewUri,
            contentDescription = "편집 사진 미리보기",
            contentScale = ContentScale.Fit,
            colorFilter = photo.filter.toColorFilter(photo.filterIntensity),
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(
                    rotationZ = photo.rotation.toFloat(),
                    scaleX = if (photo.mirrored) -1f else 1f
                )
        )
    }
}

@Composable
private fun PhotoSelector(photos: List<EditablePhoto>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        photos.forEachIndexed { index, photo ->
            AsyncImage(
                model = photo.previewUri,
                contentDescription = "사진 ${index + 1}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .border(
                        width = 2.dp,
                        color = if (index == selectedIndex) {
                            MaterialTheme.colorScheme.primary
                        } else Color.Transparent,
                        shape = RoundedCornerShape(4.dp)
                    )
                    .clip(RoundedCornerShape(4.dp))
                    .clickable { onSelect(index) }
            )
        }
    }
}

@Composable
private fun EditActions(
    onCrop: () -> Unit,
    onRotate: () -> Unit,
    onMirror: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AssistChip(onClick = onCrop, label = { Text("자르기") })
        AssistChip(onClick = onRotate, label = { Text("회전") })
        AssistChip(onClick = onMirror, label = { Text("좌우 반전") })
        AssistChip(onClick = onReset, label = { Text("초기화") })
    }
}

@Composable
private fun FilterSelector(photo: EditablePhoto, onSelect: (PhotoFilter) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        PhotoFilter.entries.forEach { filter ->
            Column(
                modifier = Modifier.clickable { onSelect(filter) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AsyncImage(
                    model = photo.previewUri,
                    contentDescription = "${filter.label} 필터",
                    contentScale = ContentScale.Crop,
                    colorFilter = filter.toColorFilter(1f),
                    modifier = Modifier
                        .size(54.dp)
                        .border(
                            2.dp,
                            if (photo.filter == filter) MaterialTheme.colorScheme.primary
                            else Color.Transparent,
                            RoundedCornerShape(6.dp)
                        )
                        .clip(RoundedCornerShape(6.dp))
                )
                Text(
                    text = filter.label,
                    fontWeight = if (photo.filter == filter) FontWeight.Bold else FontWeight.Normal,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun FilterIntensity(photo: EditablePhoto, onChange: (Float) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text("강도", style = MaterialTheme.typography.labelMedium)
        Spacer(Modifier.width(8.dp))
        Slider(
            value = photo.filterIntensity,
            onValueChange = onChange,
            enabled = photo.filter != PhotoFilter.ORIGINAL,
            modifier = Modifier.weight(1f)
        )
        val percent = if (photo.filter == PhotoFilter.ORIGINAL) 0 else {
            (photo.filterIntensity * 100).toInt()
        }
        Text("$percent%")
    }
}

private fun PhotoFilter.toColorFilter(intensity: Float): ColorFilter =
    ColorFilter.colorMatrix(ColorMatrix(PhotoFilterMatrix.values(this, intensity)))
