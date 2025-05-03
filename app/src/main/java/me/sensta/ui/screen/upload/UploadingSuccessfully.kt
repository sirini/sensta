package me.sensta.ui.screen.upload

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.theme.robotoSlabFontFamily
import me.sensta.viewmodel.local.LocalCommonViewModel
import me.sensta.viewmodel.local.LocalUploadViewModel

@Composable
fun UploadingDone() {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val uploadViewModel = LocalUploadViewModel.current
    val uploadedPostUid by uploadViewModel.uploadedPostUid

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "UPLOADED SUCCESSFULLY",
            modifier = Modifier.padding(bottom = 16.dp),
            fontFamily = robotoSlabFontFamily
        )

        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Upload",
            modifier = Modifier.size(120.dp),
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "업로드를 완료하였습니다!",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            commonViewModel.updatePostUid(uploadedPostUid)
            navController.navigate(Screen.View.route) {
                launchSingleTop = true
                restoreState = true
            }
        }, modifier = Modifier.fillMaxWidth()) {
            Text(text = "게시글 보러가기")
        }
    }
}