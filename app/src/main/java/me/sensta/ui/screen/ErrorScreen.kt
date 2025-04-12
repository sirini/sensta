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
import me.sensta.ui.theme.robotoSlabFontFamily

@Composable
fun ErrorScreen() {
    val context = LocalContext.current

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "내부 오류 발생: 데이터를 가져오지 못했습니다.")
            Text(
                text = Env.domain,
                fontFamily = robotoSlabFontFamily,
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