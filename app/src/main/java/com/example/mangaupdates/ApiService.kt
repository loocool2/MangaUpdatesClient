package com.example.mangaupdates

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler

import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

import com.google.gson.annotations.SerializedName
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT

interface MangaUpdatesApi {
    @PUT("account/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    @POST("series/search")
    suspend fun searchManga(@Body request: SearchRequest): SearchResponse

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
    val uid: Long,
    val username: String?
)

data class SearchRequest(
    val search: String
)

data class SearchResponse(
    @SerializedName("results") val results: List<ResultItem>
)

data class ResultItem(
    @SerializedName("record") val record: SeriesInfo
)

@Parcelize
data class Genre(
    val genre: String
) : Parcelable

@Parcelize
data class SeriesInfo(
    val title: String,
    val description: String,
    val year: String,
    val type: String,
    val image: ImageInfo,
    val genres: List<Genre>,
    val metadata: Metadata? = null
) : Parcelable

@Parcelize
data class ImageInfo(
    val url: ImageUrls
) : Parcelable

@Parcelize
data class ImageUrls(
    val original: String,
    val thumb: String
) : Parcelable

@Parcelize
data class Metadata(
    val user_list: UserList? = null
) : Parcelable

@Parcelize
data class UserList(
    val status: UserStatus? = null
) : Parcelable

@Parcelize
data class UserStatus(
    val volume: Int? = null,
    val chapter: Int? = null,
    val score: Int? = null
) : Parcelable
}