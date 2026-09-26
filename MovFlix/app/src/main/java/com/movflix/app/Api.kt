package com.movflix.app

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

object ApiClient {
    // Apne server ka URL yahan daalein
    private const val BASE_URL = "https://movflix-16ow.onrender.com/" 

    val service: MovFlixApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MovFlixApi::class.java)
    }
}

interface MovFlixApi {
    @GET("trending")
    suspend fun getTrending(@Query("page") page: Int = 1): MediaResponse

    @GET("discover/movie")
    suspend fun getMovies(@Query("page") page: Int = 1, @Query("genre") genre: Int = 0): MediaResponse

    @GET("discover/tv")
    suspend fun getTv(@Query("page") page: Int = 1, @Query("genre") genre: Int = 0): MediaResponse

    @GET("anime")
    suspend fun getAnime(@Query("page") page: Int = 1): MediaResponse

    @GET("search")
    suspend fun search(@Query("q") query: String, @Query("page") page: Int = 1): MediaResponse

    @GET("detail/{type}/{id}")
    suspend fun getDetail(@Path("type") type: String, @Path("id") id: Long): MediaDetail
}

data class MediaItem(
    val id: Long,
    val title: String?,
    val name: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    @SerializedName("media_type") val mediaType: String?
) {
    val displayTitle: String get() = title ?: name ?: "Unknown"
    val year: String get() = (releaseDate ?: firstAirDate ?: "").take(4)
}

data class MediaResponse(
    val page: Int,
    val results: List<MediaItem>,
    @SerializedName("total_pages") val totalPages: Int
)

data class MediaDetail(
    val id: Long,
    val title: String?,
    val name: String?,
    val overview: String?,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("backdrop_path") val backdropPath: String?,
    @SerializedName("vote_average") val voteAverage: Double?,
    @SerializedName("release_date") val releaseDate: String?,
    @SerializedName("first_air_date") val firstAirDate: String?,
    val runtime: Int?,
    val genres: List<Genre>?,
    val credits: Credits?,
    @SerializedName("stream_url") val streamUrl: String?
)

data class Genre(val id: Int, val name: String)
data class Credits(val cast: List<Cast>?)
data class Cast(
    val name: String,
    val character: String?,
    @SerializedName("profile_path") val profilePath: String?
)
