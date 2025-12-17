package com.example.petcare.domain.use_case.add_medication
import androidx.compose.ui.geometry.Rect
import com.example.petcare.common.Resource
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationEventRepository
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.exceptions.Failure
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime
import javax.inject.Inject


class AddMedicationUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository,
    private val medicationEventRepository: IMedicationEventRepository,
    private val petMemberRepository: IPetMemberRepository
){
    operator fun invoke(
        name: String,
        form: String?,
        dose: String?,
        notes: String?,
        from: LocalDate,
        to: LocalDate?,
        reccurenceString: String,
        times: List<Instant>
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading())
       val userId = userProvider.getUserId();
        if(userId == null){
            emit(Resource.Error("User not logged in"));
            return@flow
        }
        val petId = petProvider.getCurrentPetId();
        if(petId == null){
            emit(Resource.Error("No pet selected"));
            return@flow
        }
        if (!petMemberRepository.isUserPetMember(userId, petId)) {
            emit(Resource.Error("User does not have permission to add medication for this pet"))
            return@flow
        }
        val medication = Medication(
            id = UUID.randomUUID().toString(),
            petId = petId,
            name = name,
            form = form,
            dose = dose,
            notes = notes,
            active = true,
            createdAt = DateConverter.localDateNow(),
            from = from,
            to = to,
            reccurenceString = reccurenceString,
            times = times
        )
        try {
            medicationRepository.createMedication(medication)
            medicationEventRepository.createByMedication(medication);

            emit(Resource.Success(Unit))
            return@flow
        } catch (e: Failure) {
            emit(Resource.Error(e.message ?: "An unexpected error occurred"))
        }

    }
}