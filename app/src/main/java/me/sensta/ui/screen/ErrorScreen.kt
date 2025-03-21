package me.sensta.ui.screen

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import me.data.env.Env
import me.sensta.ui.theme.titleFontFamily

@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "죄송합니다. 잠시 서버와의 연결이 원할하지 않습니다")
            Text(
                text = Env.domain,
                fontFamily = titleFontFamily,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )
            Button(onClick = {
                val intent = Intent(Intent.ACTION_VIEW, Env.domain.toUri())
                context.startActivity(intent)
            }) {
                Text(text = "사이트 열어보기")
            }
        }
    }
}