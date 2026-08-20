package me.sensta.ui.screen.upload

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextButton
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
import androidx.core.net.toUri
import me.data.env.Env
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
    val isPolicyAccepted by uploadViewModel.isCommunityPolicyAccepted
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

        if (!isPolicyAccepted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = false,
                    onCheckedChange = uploadViewModel::acceptCommunityPolicy
                )
                Text("게시물 운영 원칙을 확인하고 동의합니다")
            }
            Row(horizontalArrangement = Arrangement.Center) {
                TextButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, "${Env.DOMAIN}/terms".toUri()))
                }) {
                    Text("운영 원칙 보기")
                }
                TextButton(onClick = {
                    context.startActivity(Intent(Intent.ACTION_VIEW, "${Env.DOMAIN}/privacy".toUri()))
                }) {
                    Text("개인정보 처리방침")
                }
            }
        }

        UploadBottomRow(onBack = { uploadViewModel.setUploadState(UploadState.InputContent) }) {
            if (!isPolicyAccepted) {
                Toast.makeText(context, "게시물 운영 원칙에 동의해주세요.", Toast.LENGTH_SHORT).show()
            } else if (tags.isEmpty()) {
                Toast.makeText(context, "태그를 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                uploadViewModel.setUploadState(UploadState.UploadCompleted)
            }
        }
    }
}
