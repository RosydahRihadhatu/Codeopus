package com.example.codeopus.api

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface GPTApiService {
    @Headers("Content-Type: application/json", "Authorization: Bearer sk......(secret key api gpt)")
    @POST("v1/chat/completions")
    fun translateCode(@Body requestBody: GPTChatRequest): Call<GPTChatResponse>
}
