import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Walk
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IPetRepository
import kotlinx.coroutines.delay
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import javax.inject.Inject


class GetWalkUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val petRepository: IPetRepository
) {
    operator fun invoke(
        petId: UUID,
        delayMs: Long = 600,
        shouldFail: Boolean = false,
    ): Flow<Resource<List<Walk>>> = flow {
        emit(Resource.Loading<List<Walk>>())
        delay(delayMs)
        if (shouldFail) {
            emit(Resource.Error<List<Walk>>("Failed to get walks"))

        } else {
            val walk1 = Walk(
                id = UUID.randomUUID(),
                pet_id = petProvider.getCurrentPetId(),
                started_at = Instant.parse("2024-01-01T10:00:00Z"),
                ended_at = Instant.parse("2024-01-01T10:30:00Z"),
                duration_sec = 1800,
                distance_meters = 1500,
                steps = 2000,
                createdAt = Clock.System.now(),
            )
            val walk2 = Walk(
                id = UUID.randomUUID(),
                pet_id = petProvider.getCurrentPetId(),
                started_at = Instant.parse("2024-01-02T11:00:00Z"),
                ended_at = Instant.parse("2024-01-02T11:45:00Z"),
                duration_sec = 2700,
                distance_meters = 2500,
                steps = 3500,
                createdAt = Clock.System.now(),
            )
            val walks = listOf(walk1, walk2)
            emit(Resource.Success<List<Walk>>(walks))
        }
    }
}