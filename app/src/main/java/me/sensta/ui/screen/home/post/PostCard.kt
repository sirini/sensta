package me.sensta.ui.screen.home.post

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardPost
import me.sensta.ui.navigation.Screen
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun PostCard(post: TsboardPost) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val moveToView: () -> Unit = {
        commonViewModel.updatePostUid(post.uid)
        navController.navigate(Screen.View.route) {
            launchSingleTop = true
            restoreState = true
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        PostCardHeader(writer = post.writer)
        FeedCover(path = post.cover, title = post.title, onClick = moveToView)
        PostCardFooter(post = post, onViewClick = moveToView)
        Spacer(modifier = Modifier.height(18.dp))
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 18.dp),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}
