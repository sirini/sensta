package me.sensta.ui.screen.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.data.env.Env
import me.sensta.ui.theme.titleFontFamily
import me.sensta.viewmodel.PhotoListViewModel

@Composable
fun PhotoError(viewModel: PhotoListViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "서버와의 연결이 원할하지 않습니다")
            Text(
                text = Env.domain,
                fontFamily = titleFontFamily,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            Button(onClick = {
                viewModel.refresh()
            }) {
                Text(text = "다시 시도하기")
            }
        }
    }
}