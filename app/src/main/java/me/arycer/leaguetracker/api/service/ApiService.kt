package me.arycer.leaguetracker.api.service

import me.arycer.leaguetracker.api.model.UserProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/profiles/{region}/{accountName}/{tagline}")
    suspend fun getProfile(
        @Path("region") region: String,
        @Path("accountName") accountName: String,
        @Path("tagline") tagline: String
    ): Response<UserProfile>
}