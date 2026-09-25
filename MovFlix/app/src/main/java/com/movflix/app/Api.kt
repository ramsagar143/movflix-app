package com.movflix.app

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

const val IMG_BASE = "https://image.tmdb.org/t/p/"

data class MediaResponse(val results: List<Media>? = null)

data class Media(
    val id: Int = 0,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null
) {
    val displayTitle: String get() = title ?: name ?: "Unknown"
    val type: String get() = when {
        mediaType == "tv" -> "tv"
        mediaType == "movie" -> "movie"
        name != null -> "tv"
        else -> "movie"
    }
}

data class Genre(val id: Int = 0, val name: String? = null)
data class Video(val key: String? = null, val site: String? = null, val type: String? = null)
data class Videos(val results: List<Video>? = null)

data class Detail(
    val id: Int = 0,
    val title: String? = null,
    val name: String? = null,
    val overview: String? = null,
    val runtime: Int? = null,
    val genres: List<Genre>? = null,
    val videos: Videos? = null,
    @SerializedName("poster_path") val posterPath: String? = null,
    @SerializedName("backdrop_path") val backdropPath: String? = null,
    @SerializedName("vote_average") val voteAverage: Double? = null,
    @SerializedName("release_date") val releaseDate: String? = null,
    @SerializedName("first_air_date") val firstAirDate: String? = null
) {
    val displayTitle: String get() = title ?: name ?: "Unknown"
    fun trailerKey(): String? =
        videos?.results?.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }?.key
            ?: videos?.results?.firstOrNull { it.site == "YouTube" }?.key
}

interface TmdbService {
    @GET("trending") fun trending(): Call<MediaResponse>
    @GET("discover/movie") fun movies(): Call<MediaResponse>
    @GET("discover/tv") fun shows(): Call<MediaResponse>
    @GET("anime") fun anime(): Call<MediaResponse>
    @GET("search") fun search(@Query("q") q: String): Call<MediaResponse>
    @GET("detail/{type}/{id}") fun detail(@Path("type") type: String, @Path("id") id: Int): Call<Detail>
}

object Api {
    val service: TmdbService by lazy {
        Retrofit.Builder()
            .baseUrl(BuildConfig.BACKEND_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmdbService::class.java)
    }

    fun img(path: String?, size: String = "w500"): String? =
        if (path.isNullOrEmpty()) null else IMG_BASE + size + path
}
