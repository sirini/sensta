package me.sensta.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.domain.model.board.NuboStudioPost
import me.domain.model.board.NuboStudioSort
import me.domain.model.board.NuboStudioSummary
import me.domain.repository.NuboResponse
import me.domain.usecase.auth.GetUserInfoUseCase
import me.domain.usecase.board.GetMyStudioUseCase
import me.sensta.diagnostics.AppDiagnostics
import javax.inject.Inject

data class ProfileStudioUiState(
    val summary: NuboStudioSummary = NuboStudioSummary(),
    val posts: List<NuboStudioPost> = emptyList(),
    val selectedSort: NuboStudioSort = NuboStudioSort.Recent,
    val page: Int = 0,
    val hasNext: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProfileStudioViewModel @Inject constructor(
    private val getMyStudioUseCase: GetMyStudioUseCase,
    private val getUserInfoUseCase: GetUserInfoUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileStudioUiState())
    val uiState = _uiState.asStateFlow()

    private var loadJob: Job? = null

    fun refresh() {
        loadPage(page = 1, reset = true)
    }

    fun selectSort(sort: NuboStudioSort) {
        if (_uiState.value.selectedSort == sort) return
        loadJob?.cancel()
        _uiState.value = ProfileStudioUiState(
            summary = _uiState.value.summary,
            selectedSort = sort
        )
        loadPage(page = 1, reset = true)
    }

    fun loadMore() {
        val state = _uiState.value
        if (state.isLoading || state.isLoadingMore || !state.hasNext) return
        loadPage(page = state.page + 1, reset = false)
    }

    private fun loadPage(page: Int, reset: Boolean) {
        if (!reset && _uiState.value.isLoadingMore) return
        if (reset) loadJob?.cancel()

        _uiState.value = if (reset) {
            _uiState.value.copy(
                posts = emptyList(),
                page = 0,
                hasNext = false,
                isLoading = true,
                isLoadingMore = false,
                error = null
            )
        } else {
            _uiState.value.copy(isLoadingMore = true, error = null)
        }

        loadJob = viewModelScope.launch {
            val token = getUserInfoUseCase().first().token
            if (token.isBlank()) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    error = "로그인이 필요합니다."
                )
                return@launch
            }

            getMyStudioUseCase(
                page = page,
                limit = PAGE_SIZE,
                sort = _uiState.value.selectedSort,
                token = token
            ).collect { response ->
                when (response) {
                    is NuboResponse.Loading -> Unit
                    is NuboResponse.Error -> {
                        AppDiagnostics.report("내 작품 스튜디오", response)
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = "작품 정보를 불러오지 못했습니다."
                        )
                    }
                    is NuboResponse.Success -> {
                        val studio = response.data
                        val current = if (reset) emptyList() else _uiState.value.posts
                        _uiState.value = _uiState.value.copy(
                            summary = studio.summary,
                            posts = (current + studio.posts.items).distinctBy { it.uid },
                            page = studio.posts.page,
                            hasNext = studio.posts.hasNext,
                            isLoading = false,
                            isLoadingMore = false,
                            error = null
                        )
                    }
                }
            }
        }
    }

    private companion object {
        const val PAGE_SIZE = 20
    }
}
