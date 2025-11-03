package com.example.petcare.domain.providers

import java.util.UUID

interface IPetProvider {
     fun getCurrentPetId(): UUID;
        fun setCurrentPetId(id: UUID);
}
