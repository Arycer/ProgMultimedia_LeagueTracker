package me.arycer.leaguetracker.model

class FavouriteUser(
    val name: String,
    val tagline: String,
    val region: Region,
) {
    override fun toString(): String {
        return "FavouriteUser(name='$name', tagline='$tagline', region=$region)"
    }

    enum class Region(val descriptor: String) {
        EUW("Europe West"),
        EUNE("Europe Northern-East"),
        KR("Korea"),
        BR("Brazil"),
        LAN("Latin America North"),
        LAS("Latin America South")
    }

    data class RankedInfo(
        val queue: String,
        val tier: String,
        val rank: String,
        val lp: Int,
        val wins: Int,
        val losses: Int
    )
}