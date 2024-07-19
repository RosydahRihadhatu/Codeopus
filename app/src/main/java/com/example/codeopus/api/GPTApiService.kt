package com.example.codeopus.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GPTApiService {
    @Headers("Content-Type: application/json", "Authorization: Bearer sk-proj-H0OjBhIUd7N3JjiAxzxLT3BlbkFJgKYVflpKncrLwPCmoKCM")
    @POST("v1/chat/completions")
    fun translateCode(@Body requestBody: GPTChatRequest): Call<GPTChatResponse>
}
