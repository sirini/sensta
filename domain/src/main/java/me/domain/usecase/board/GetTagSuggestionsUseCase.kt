package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

class GetTagSuggestionsUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(query: String, limit: Int, token: String) = flow {
        emit(repository.getTagSuggestions(query, limit, token))
    }
}
