package com.example.mangaupdates

import android.os.Parcel
import android.os.Parcelable
import kotlinx.parcelize.Parceler

import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.TypeParceler

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

object RetrofitInstance {
    val api: MangaUpdatesApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.mangaupdates.com/v1/") // make sure it ends with /
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MangaUpdatesApi::class.java)
    }
}


interface MangaUpdatesApi {
    @PUT("account/login")
    @Headers("Content-Type: application/json")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    @POST("series/search")
    suspend fun searchManga(@Body request: SearchRequest): SearchResponse
    @GET("lists/series/{series_id}")
    @Headers("Content-Type: application/json")

    suspend fun getSeriesListStatus(
        @Header("Authorization") auth: String,
        @Path("series_id") seriesId: Long
    ): SeriesListStatus

    @POST("lists/{user_id}/search")
    @Headers("Content-Type: application/json")
    suspend fun getUserLists(
        @Header("Authorization") auth: String,
        @Path("user_id") userId: Long,
        @Body body: ListSearchRequest = ListSearchRequest()
    ): ListSearchResponse

    @POST("lists/series")
    @Headers("Content-Type: application/json")
    suspend fun addSeriesToList(
        @Header("Authorization") auth: String,
        @Body payload: List<AddSeriesRequest>
    ): Unit

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
    @SerializedName("series_id")
    val id: Long,
    val title: String,
    val description: String,
    val year: String,
    val type: String,
    val image: ImageInfo,
    val genres: List<Genre>,
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
data class UserList(
    val status: UserStatus? = null
) : Parcelable

@Parcelize
data class UserStatus(
    val volume: Int? = null,
    val chapter: Int? = null,
    val score: Int? = null
) : Parcelable

data class SeriesListStatus(
    val series: SeriesRef,
    val list_id: Long?,
    val list_type: String?,
    val list_icon: String?,
    val status: ReadingStatus?,
    val priority: Int?,
    val time_added: TimeInfo?
)

data class SeriesRef(
    val id: Long,
    val url: String,
    val title: String
)

data class ReadingStatus(
    val volume: Int?,
    val chapter: Int?
)

data class TimeInfo(
    val timestamp: Long,
    val as_rfc3339: String,
    val as_string: String
)

    data class ListSearchRequest(
        val page: Int = 1,
        val per_page: Int = 50
    )

    data class ListSearchResponse(
        val total_hits: Int,
        val page: Int,
        val per_page: Int,
        val list: ListInfo,
        val results: List<ListItem>
    )

    data class ListInfo(
        val list_id: Long,
        val title: String
    )

    data class ListItem(
        val record: SeriesListStatus  // re-use your existing SeriesListStatus here
    )

    data class AddSeriesRequest(
        val series: SeriesRef,
        val list_id: Long,
        val status: AddStatus = AddStatus(),
        val priority: Int = 0
    )

    data class AddStatus(
        val volume: Int = 0,
        val chapter: Int = 0
    )


}