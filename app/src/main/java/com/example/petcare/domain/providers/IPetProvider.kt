package com.example.petcare.domain.providers

import java.util.UUID

interface IPetProvider {
     fun getCurrentPetId(): String;
        fun setCurrentPetId(id: String);
}
