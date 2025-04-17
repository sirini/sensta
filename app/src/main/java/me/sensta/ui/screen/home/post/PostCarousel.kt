package me.sensta.ui.screen.home.post

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.accompanist.pager.ExperimentalPagerApi
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.google.accompanist.pager.rememberPagerState
import kotlinx.coroutines.flow.distinctUntilChanged
import me.data.env.Env
import me.domain.model.photo.TsboardImage
import me.sensta.viewmodel.local.LocalCommonViewModel

@OptIn(ExperimentalPagerApi::class)
@Composable
fun PostCarousel(images: List<TsboardImage>) {
    val pagerState = rememberPagerState()
    val commonViewModel = LocalCommonViewModel.current

    // 보고 있는 페이지가 변경되면 인덱스를 공용 뷰모델에 저장
    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }
            .distinctUntilChanged()
            .collect { page -> commonViewModel.updatePagerIndex(page) }
    }

    // 게시글 번호가 바뀌면 pagerState 초기화
    LaunchedEffect(commonViewModel.postUid) {
        pagerState.scrollToPage(0)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
    ) {
        HorizontalPager(
            count = images.size,
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
        ) { page ->
            AsyncImage(
                model = Env.domain + images[page].thumbnail.large,
                contentDescription = "Image ${page + 1}",
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(onDoubleTap = {
                            commonViewModel.openFullScreen(
                                imagePath = images[page].thumbnail.large,
                            )
                        })
                    },
                contentScale = ContentScale.Crop
            )
        }

        if (images.size > 1) {
            HorizontalPagerIndicator(
                pagerState = pagerState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                activeColor = Color.White,
                indicatorWidth = 4.dp,
                indicatorHeight = 4.dp,
                inactiveColor = Color.LightGray,
            )
        }

    }
}