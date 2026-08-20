package me.sensta.ui.screen.home.post

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import kotlinx.coroutines.flow.distinctUntilChanged
import me.data.env.Env
import me.domain.model.photo.TsboardImage
import me.sensta.viewmodel.local.LocalCommonViewModel

@Composable
fun PostCarousel(images: List<TsboardImage>) {
    val pagerState = rememberPagerState(0) { images.size }
    val commonViewModel = LocalCommonViewModel.current

    // 보고 있는 페이지가 변경되면 인덱스를 공용 뷰모델에 저장한다.
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> commonViewModel.updatePagerIndex(page) }
    }

    // 게시글 번호가 바뀌면 첫 사진으로 돌아간다.
    LaunchedEffect(commonViewModel.postUid) {
        pagerState.scrollToPage(0)
    }

    Box(modifier = Modifier.fillMaxWidth().aspectRatio(0.75f)) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxWidth()) { page ->
            AsyncImage(
                model = Env.DOMAIN + images[page].thumbnail.large,
                contentDescription = "Image ${page + 1}",
                modifier = Modifier.fillMaxSize().pointerInput(images[page].thumbnail.large) {
                    detectTapGestures {
                        commonViewModel.openFullScreen(images[page].thumbnail.large)
                    }
                },
                contentScale = ContentScale.Crop
            )
        }

        if (images.size > 1) {
            Row(
                modifier = Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                repeat(pagerState.pageCount) { index ->
                    Box(
                        modifier = Modifier
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(if (pagerState.currentPage == index) Color.White else Color.Gray)
                            .size(4.dp)
                    )
                }
            }
        }
    }
}
