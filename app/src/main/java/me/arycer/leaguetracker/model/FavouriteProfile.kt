package me.arycer.leaguetracker.model

import java.util.UUID

class FavouriteProfile(
    var id: String,
    var name: String,
    var tagline: String,
    var region: Region,
) {

    override fun toString(): String {
        return "FavouriteUser(name='$name', tagline='$tagline', region=$region)"
    }

    constructor() : this(UUID.randomUUID().toString(), "", "", Region.EUW)

    enum class Region(val descriptor: String) {
        EUW("Europe West"),
        EUNE("Europe Northern-East"),
        KR("Korea"),
        BR("Brazil"),
        LAN("Latin America North"),
        LAS("Latin America South")
    }
}