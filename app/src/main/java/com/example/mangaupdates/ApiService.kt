package com.example.mangaupdates

import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.PUT

interface MangaUpdatesApi {
    @PUT("account/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val status: String,
    val reason: String,
    val context: LoginContext?
)

data class LoginContext(
    val session_token: String,
    val uid: Long
)