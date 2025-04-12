package me.sensta.ui.screen.profile

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.yalantis.ucrop.UCrop
import me.data.env.Env
import me.sensta.viewmodel.common.LocalAuthViewModel
import java.io.File

@Composable
fun ProfileViewImage() {
    val authViewModel = LocalAuthViewModel.current
    val context = LocalContext.current
    val user by authViewModel.user.collectAsState()

    // 선택한 이미지를 편집한 결과를 받는 런처
    val cropLauncher = rememberLauncherForActivityResult(
        contract = object : ActivityResultContract<Intent, Uri?>() {
            override fun createIntent(context: Context, input: Intent): Intent = input
            override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
                return if (resultCode == Activity.RESULT_OK) {
                    UCrop.getOutput(intent!!)
                } else null
            }
        },
        onResult = { croppedUri ->
            croppedUri?.let {
                authViewModel.updateProfileImage(uri = croppedUri, context = context)
            }
        }
    )

    // 프로필 이미지 선택하는 런처
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped_image.jpg"))
            val uCrop = UCrop.of(uri, destinationUri)
                .withAspectRatio(1f, 1f)  // 정사각형 비율
                .withMaxResultSize(256, 256)

            val uCropIntent = uCrop.getIntent(context)
            cropLauncher.launch(uCropIntent)
        }
    }

    // 갤러리 접근 권한 요청 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            imagePickerLauncher.launch("image/*")
        } else {
            Toast.makeText(context, "갤러리에 접근할 권한을 허용해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    Box(
        modifier = Modifier
            .width(180.dp)
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        // 프로필 이미지 혹은 빈 아이콘 보여주기
        AsyncImage(
            model = Env.domain + user.profile,
            contentDescription = user.name,
            modifier = Modifier
                .size(160.dp)
                .clip(CircleShape)
        )

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.onBackground)
                    .clickable {
                        launcher.launch(android.Manifest.permission.READ_MEDIA_IMAGES)
                    }, contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit profile",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}