package me.sensta.ui.screen.explorer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sensta.viewmodel.local.LocalExplorerViewModel

@Composable
fun SearchBox() {
    val explorerViewModel = LocalExplorerViewModel.current
    val keyword by explorerViewModel.keyword
    val options = listOf(
        explorerViewModel.aiDescOption to "AI 설명",
        explorerViewModel.hashtagOption to "태그",
        explorerViewModel.writerOption to "작성자",
        explorerViewModel.titleOption to "제목",
        explorerViewModel.contentOption to "내용",
    )
    val option by explorerViewModel.option
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
        OutlinedTextField(
            value = keyword,
            onValueChange = { explorerViewModel.setKeyword(it) },
            label = { Text("검색어를 입력하세요") },
            singleLine = true,
            maxLines = 1,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Row(
                    modifier = Modifier
                        .clickable { expanded = true }
                        .padding(start = 16.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = options.first { it.first == option }.second)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "option"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    explorerViewModel.search(
                        option = option,
                        keyword = keyword
                    )
                }) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "image search",
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp)
                    )
                }
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth(0.3f),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            options.forEach { opt ->
                DropdownMenuItem(
                    onClick = {
                        explorerViewModel.setOption(opt.first)
                        expanded = false
                    }, text = { Text(text = opt.second) }
                )
            }
        }
    }
}