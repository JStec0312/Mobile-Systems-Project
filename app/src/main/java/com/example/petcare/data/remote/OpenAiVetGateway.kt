package com.example.petcare.data.remote

import com.example.petcare.BuildConfig
import com.example.petcare.domain.model.Pet
import com.example.petcare.domain.remote.IVetAiGateway
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import org.json.JSONObject   // dodamy zaraz zaleznosc
import javax.inject.Inject

class OpenAiVetGateway @Inject constructor(
    private val httpClient: HttpClient,
) : IVetAiGateway {

    private val apiKey: String = BuildConfig.OPENAI_API_KEY;
    override suspend fun askVetAiQuestion(
        question: String,
        pet: Pet
    ): String {
        val systemPrompt = buildSystemPrompt()
        val userPrompt = buildUserPrompt(question, pet)

        return callAiApi(systemPrompt, userPrompt)
    }

    private fun buildSystemPrompt(): String = """
        You are vetAInary, a virtual veterinary assistant.
        You behave like a veterinarian but you are NOT a real doctor.
        Rules:
        - You NEVER give a final diagnosis.
        - You NEVER prescribe or change medications directly.
        - You ALWAYS recommend consulting a real veterinarian.
        - You explain possible causes in simple language.
        - If symptoms sound urgent, clearly mark that the situation may be an emergency.
    """.trimIndent()

    private fun buildUserPrompt(
        query: String,
        pet: Pet?
    ): String {
        val petInfo = if (pet != null) {
            """
            Pet info:
            - species: ${pet.species}
            - breed: ${pet.breed}
            - born: ${pet.birthDate}
            - sex: ${pet.sex}
            """.trimIndent()
        } else {
            "Pet info: not provided."
        }

        return """
            $petInfo

            Owner question:
            $query
        """.trimIndent()
    }

    private fun String.toJsonEscaped(): String =
        this.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

    private fun buildChatRequestJson(systemPrompt: String, userPrompt: String): String {
        return """
            {
              "model": "gpt-4.1-mini",
              "messages": [
                {
                  "role": "system",
                  "content": "${systemPrompt.toJsonEscaped()}"
                },
                {
                  "role": "user",
                  "content": "${userPrompt.toJsonEscaped()}"
                }
              ]
            }
        """.trimIndent()
    }

    private fun extractContentFromResponse(json: String): String {
        return try {
            val root = JSONObject(json)
            val choices = root.optJSONArray("choices") ?: return "Sorry, I could not generate a response."
            if (choices.length() == 0) return "Sorry, I could not generate a response."

            val first = choices.getJSONObject(0)
            val message = first.optJSONObject("message") ?: return "Sorry, I could not generate a response."
            message.optString("content", "Sorry, I could not generate a response.")
        } catch (e: Exception) {
            "Sorry, I could not generate a response."
        }
    }

    private suspend fun callAiApi(systemPrompt: String, userPrompt: String): String {
        val requestJson = buildChatRequestJson(systemPrompt, userPrompt)

        val responseText: String =
            httpClient.post("https://api.openai.com/v1/chat/completions") {
                header("Authorization", "Bearer $apiKey")
                contentType(ContentType.Application.Json)
                setBody(requestJson) // DAJEMY STRINGA, NIE OBIEKT
            }.bodyAsText()

        return extractContentFromResponse(responseText)
    }
}
