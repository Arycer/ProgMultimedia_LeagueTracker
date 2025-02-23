package me.arycer.leaguetracker.api.service

import me.arycer.leaguetracker.api.model.UserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("api/profiles/{region}/{accountName}/{tagline}")
    suspend fun getProfile(
        @Path("region") region: String,
        @Path("accountName") accountName: String,
        @Path("tagline") tagline: String
    ): Response<UserProfile>

    @GET("api/matches/{region}/{puuid}")
    suspend fun getMatches(
        @Path("region") region: String,
        @Path("name") name: String,
        @Path("tagline") tagline: String,
        @Query("startTime") startTime: Long? = null,
        @Query("endTime") endTime: Long? = null,
        @Query("queue") queue: Int? = null,
        @Query("start") start: Int = 0,
        @Query("count") count: Int = 20
    ): Response<List<String>>
}