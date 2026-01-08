package com.example.petcare.integration.data

import com.example.petcare.common.sexEnum
import com.example.petcare.common.speciesEnum
import com.example.petcare.data.remote.OpenAiVetGateway
import com.example.petcare.domain.model.Pet
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.ktor.client.HttpClient
import junit.framework.TestCase.assertNotNull
import org.junit.Rule
import javax.inject.Inject
import org.junit.Test
import kotlinx.coroutines.runBlocking
import kotlinx.datetime.LocalDate


@HiltAndroidTest
class OpenAiIntegrationTest {
    private val httpClient = HttpClient()
    private val gateway = OpenAiVetGateway(httpClient)

    @Test
    fun openai_api_returns_a_non_empty_response() = runBlocking {

        val pet = Pet(
            id = "pet-test",
            ownerUserId = "owner-test",
            name = "Buddy",
            species = speciesEnum.dog,
            breed = "Mixed",
            sex = sexEnum.male,
            birthDate = LocalDate(2020, 1, 1),
            avatarThumbUrl = null,
            createdAt = LocalDate(2025, 12, 20)
        )

        val question = "Is my pet healthy if it sneezes occasionally?"
        val answer = gateway.askVetAiQuestion(question, pet)

        println("OpenAI response: $answer")

        assertNotNull("Expected non-null response from OpenAI", answer)
        assert( answer!!.isNotBlank())
    }
}