package me.domain.usecase.board

import kotlinx.coroutines.flow.flow
import me.domain.model.board.NuboStudioParam
import me.domain.model.board.NuboStudioSort
import me.domain.repository.NuboBoardRepository
import javax.inject.Inject

class GetMyStudioUseCase @Inject constructor(
    private val repository: NuboBoardRepository
) {
    operator fun invoke(
        page: Int,
        limit: Int,
        sort: NuboStudioSort,
        token: String
    ) = flow {
        emit(
            repository.getMyStudio(
                NuboStudioParam(
                    page = page,
                    limit = limit,
                    sort = sort,
                    token = token
                )
            )
        )
    }
}
