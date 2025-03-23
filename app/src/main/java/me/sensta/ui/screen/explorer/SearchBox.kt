package me.sensta.ui.screen.explorer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ExperimentalMaterialApi
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
import androidx.hilt.navigation.compose.hiltViewModel
import me.sensta.viewmodel.ExplorerViewModel

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchBox() {
    val explorerViewModel: ExplorerViewModel = hiltViewModel()
    val options = listOf("제목", "내용", "작성자", "태그")

    var selectedOption by remember { mutableStateOf(options[0]) }
    var searchQuery by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("검색어를 입력하세요") },
            singleLine = true,
            maxLines = 1,
            shape = MaterialTheme.shapes.large,
            leadingIcon = {
                Row(
                    modifier = Modifier
                        .clickable { isExpanded = true }
                        .padding(start = 16.dp, end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = selectedOption)
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "option"
                    )
                }
            },
            trailingIcon = {
                IconButton(onClick = {
                    explorerViewModel.search(
                        option = options.indexOf(selectedOption),
                        keyword = searchQuery
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
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier.fillMaxWidth(0.3f),
            containerColor = MaterialTheme.colorScheme.background
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    onClick = {
                        selectedOption = option
                        isExpanded = false
                    }, text = { Text(text = option) }
                )
            }
        }
    }
}