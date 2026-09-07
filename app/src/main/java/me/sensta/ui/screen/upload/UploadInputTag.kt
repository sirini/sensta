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
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import me.domain.model.common.NuboTag
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
    val suggestions by uploadViewModel.tagSuggestions
    val editorConfig by uploadViewModel.editorConfig
    val selectedCategoryUid by uploadViewModel.selectedCategoryUid
    val isEditorConfigLoading by uploadViewModel.isEditorConfigLoading
    val editorConfigError by uploadViewModel.editorConfigError
    val isPolicyAccepted by uploadViewModel.isCommunityPolicyAccepted
    var inputTag by remember { mutableStateOf("") }

    LaunchedEffect(inputTag) {
        uploadViewModel.updateTagSuggestionQuery(inputTag)
    }

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
                            uploadViewModel.addTag(inputTag)
                            inputTag = ""
                        }
                    )
                }
            }
        )

        if (suggestions.isNotEmpty()) {
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.take(5).forEach { suggestion ->
                    AssistChip(
                        onClick = {
                            uploadViewModel.selectTagSuggestion(suggestion)
                            inputTag = ""
                        },
                        label = { Text("#${suggestion.name} · ${suggestion.count}회") }
                    )
                }
            }
        }

        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tags.forEach { tag ->
                ViewPostTag(NuboTag(uid = 0, name = tag)) {
                    uploadViewModel.removeTag(tag)
                }
            }
        }

        when {
            isEditorConfigLoading -> Text(
                text = "업로드 설정을 확인하고 있습니다",
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall
            )
            editorConfigError != null -> TextButton(onClick = uploadViewModel::loadEditorConfig) {
                Text("업로드 설정을 불러오지 못했습니다 · 다시 시도")
            }
            editorConfig?.usesCategories == true -> {
                Text(
                    text = "카테고리",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                )
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    editorConfig?.categories.orEmpty().forEach { category ->
                        FilterChip(
                            selected = category.uid == selectedCategoryUid,
                            onClick = { uploadViewModel.selectCategory(category.uid) },
                            label = { Text(category.name) }
                        )
                    }
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
            } else if (editorConfig == null || selectedCategoryUid < 1) {
                Toast.makeText(context, "업로드 설정을 먼저 불러와 주세요.", Toast.LENGTH_SHORT).show()
                uploadViewModel.loadEditorConfig()
            } else {
                uploadViewModel.setUploadState(UploadState.UploadCompleted)
            }
        }
    }
}
