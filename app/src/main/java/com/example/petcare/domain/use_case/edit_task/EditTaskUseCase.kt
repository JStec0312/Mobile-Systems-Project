package com.example.petcare.domain.use_case.edit_task

import com.example.petcare.common.Resource
import com.example.petcare.domain.model.Task
import com.example.petcare.domain.providers.implementation.PetProvider
import com.example.petcare.domain.providers.implementation.UserProvider
import com.example.petcare.domain.repository.IPetMemberRepository
import com.example.petcare.domain.repository.ITaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditTaskUseCase  @Inject constructor(
    private val taskRepository: ITaskRepository,
    private val userProvider: UserProvider,
    private val petProvider: PetProvider,
    private val petMemberRepository: IPetMemberRepository
) {
}