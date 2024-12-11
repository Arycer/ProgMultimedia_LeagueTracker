package me.arycer.leaguetracker.api.model

data class UserProfile(
    val username: String,
    val tagline: String,
    val level: Int,
    val profileIconId: Int,
    val soloRankedInfo: RankedInfo?,
    val flexRankedInfo: RankedInfo?,
    val gameVersion: String
)