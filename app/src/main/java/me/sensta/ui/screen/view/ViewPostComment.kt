package me.sensta.ui.screen.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import me.domain.model.board.TsboardComment
import me.domain.repository.TsboardResponse
import me.sensta.ui.navigation.common.LocalNavController
import me.sensta.ui.screen.home.comment.CommentCardBody
import me.sensta.ui.screen.home.comment.CommentCardHeader
import me.sensta.ui.screen.home.post.PostCardHeader
import me.sensta.ui.screen.home.post.PostCarousel
import me.sensta.viewmodel.common.LocalCommonViewModel

@Composable
fun ViewPostComment(commentResponse: TsboardResponse<List<TsboardComment>>) {
    val navController = LocalNavController.current
    val commonViewModel = LocalCommonViewModel.current
    val photo by commonViewModel.photo
    val comments = (commentResponse as TsboardResponse.Success<List<TsboardComment>>).data

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item {
            PostCardHeader(photo = photo)
            PostCarousel(photo.images)
        }
        item {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                        contentDescription = "back"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("목록으로 돌아가기", fontWeight = FontWeight.Bold)
                }
            }
        }
        items(comments) { comment ->
            Card(
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onSecondary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                )
            ) {
                CommentCardHeader(comment)
                CommentCardBody(comment)
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}