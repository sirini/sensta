package me.sensta.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.asIntState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CommonViewModel @Inject constructor() : ViewModel() {
    private val _postUid = mutableIntStateOf(0)
    val postUid = _postUid.asIntState()

    private val _fullImagePath = mutableStateOf<String>("")
    val fullImagePath: State<String> get() = _fullImagePath

    private val _showFullScreen = mutableStateOf(false)
    val showFullScreen: State<Boolean> get() = _showFullScreen

    private val _pagerIndex = mutableIntStateOf(0)
    val pagerIndex = _pagerIndex.asIntState()

    private var _showCommentDialog = mutableStateOf(false)
    val showCommentDialog: State<Boolean> get() = _showCommentDialog

    // 이미 목록에서 가져왔던 사진 정보들 저장하기
    fun updatePostUid(postUid: Int) {
        _pagerIndex.intValue = 0
        _postUid.intValue = postUid
    }

    // 여러 장의 사진을 좌우로 넘길 때마다 페이저 인덱스도 업데이트
    fun updatePagerIndex(index: Int) {
        _pagerIndex.intValue = index
    }

    // 댓글 달기용 다이얼로그 띄우기
    fun openWriteCommentDialog(postUid: Int) {
        _postUid.intValue = postUid
        _showCommentDialog.value = true
    }

    // 댓글 달기용 다이얼로그 닫기
    fun closeWriteCommentDialog() {
        _showCommentDialog.value = false
    }

    // 이미지 전체 화면으로 보기
    fun openFullScreen(imagePath: String) {
        _fullImagePath.value = imagePath
        _showFullScreen.value = true
    }

    // 이미지 전체 화면 닫기
    fun closeFullScreen() {
        _fullImagePath.value = ""
        _showFullScreen.value = false
    }
}