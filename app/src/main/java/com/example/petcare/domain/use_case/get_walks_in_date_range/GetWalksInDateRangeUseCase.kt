package com.example.petcare.domain.use_case.get_walks_in_date_range

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Walk
import com.example.petcare.domain.repository.IWalkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject

class GetWalksInDateRangeUseCase @Inject constructor(
    private val repository: IWalkRepository
) {
    operator fun invoke(
        petId: String,
        from: LocalDate? = null,
        to: LocalDate? = null
    ) : Flow<Resource<List<Walk>>> = flow {
        emit(Resource.Loading())
        try {
            val allWalks = repository.getWalksByPetId(petId)
            val filteredWalks = allWalks.filter { walk ->
                val walkDate = walk.startedAt
                val matchesFrom = from == null || walkDate >= from
                val matchesTo = to == null || walkDate <= to
                matchesFrom && matchesTo
            }
            emit(Resource.Success(filteredWalks))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred."))
        }

    }
}