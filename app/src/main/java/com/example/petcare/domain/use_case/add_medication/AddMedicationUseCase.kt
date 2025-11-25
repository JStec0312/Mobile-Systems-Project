package com.example.petcare.domain.use_case.add_medication
import com.example.petcare.common.Resource
import com.example.petcare.common.utils.DateConverter
import com.example.petcare.domain.model.Medication
import com.example.petcare.domain.providers.IPetProvider
import com.example.petcare.domain.providers.IUserProvider
import com.example.petcare.domain.repository.IMedicationRepository
import com.example.petcare.exceptions.Failure
import java.util.UUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalDate
import javax.inject.Inject


class AddMedicationUseCase @Inject constructor(
    private val userProvider: IUserProvider,
    private val petProvider: IPetProvider,
    private val medicationRepository: IMedicationRepository
){
    operator fun invoke(
        petId: String,
        name: String,
        form: String?,
        dose: String?,
        notes: String?,
        from: LocalDate,
        to: LocalDate?
    ): Flow<Resource<Unit>> = flow {
        emit(Resource.Loading<Unit>())
        try{
            val userId = userProvider.getUserId();
            if (userId == null){
                emit(Resource.Error<Unit>("User not logged in"))
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
                to = to
            )
            medicationRepository.createMedication(medication)
        } catch (e: Failure){
            emit(Resource.Error<Unit>(e.message))
                return@flow
        }

    }
}