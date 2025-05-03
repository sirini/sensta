package me.sensta.ui.screen.upload

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Title
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalUploadViewModel
import me.sensta.viewmodel.state.UploadState

@Composable
fun UploadInputTitle() {
    val context = LocalContext.current
    val uploadViewModel = LocalUploadViewModel.current
    val title by uploadViewModel.title

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ENTER A TITLE",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily
        )

        OutlinedTextField(
            value = title,
            onValueChange = { uploadViewModel.setTitle(it) },
            label = { Text(text = "사진의 제목을 입력하세요") },
            singleLine = true,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Title,
                    contentDescription = "Title"
                )
            },
            isError = title.isNotEmpty() && title.trim().length < 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        UploadBottomRow(onBack = { uploadViewModel.setUploadState(UploadState.SelectImage) }) {
            if (title.trim().length < 2) {
                Toast.makeText(context, "사진의 제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                uploadViewModel.setUploadState(UploadState.InputContent)
            }
        }
    }
}