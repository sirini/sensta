package me.sensta.ui.screen.upload

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import me.domain.model.common.TsboardTag
import me.sensta.ui.screen.view.content.ViewPostTag
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalUploadViewModel
import me.sensta.viewmodel.state.UploadState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun UploadInputTag() {
    val context = LocalContext.current
    val uploadViewModel = LocalUploadViewModel.current
    val tags by uploadViewModel.tags
    var inputTag by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "ENTER TAGS",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily
        )

        OutlinedTextField(
            value = inputTag,
            onValueChange = { inputTag = it.lowercase() },
            label = { Text(text = "태그를 입력하세요") },
            singleLine = true,
            isError = inputTag.isNotEmpty() && inputTag.trim().length < 2,
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Tag,
                    contentDescription = "Tag"
                )
            },
            trailingIcon = {
                if (inputTag.trim().isNotEmpty()) {
                    Icon(
                        imageVector = Icons.Default.AddCircle,
                        contentDescription = "Add tag",
                        modifier = Modifier.clickable {
                            uploadViewModel.addTag(inputTag, context)
                            inputTag = ""
                        }
                    )
                }
            }
        )

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                ViewPostTag(TsboardTag(uid = 0, name = tag)) {
                    uploadViewModel.removeTag(tag)
                }
            }
        }

        UploadBottomRow(onBack = { uploadViewModel.setUploadState(UploadState.InputContent) }) {
            if (tags.isEmpty()) {
                Toast.makeText(context, "태그를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                uploadViewModel.setUploadState(UploadState.UploadCompleted)
            }
        }
    }
}