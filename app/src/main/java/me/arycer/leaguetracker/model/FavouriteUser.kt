package me.arycer.leaguetracker.model

class FavouriteUser(
    val name: String,
    val tagline: String,
    val region: Region = Region.EUW,
    val profilePictureId: Int
) {
    override fun toString(): String {
        return "FavouriteUser(name='$name', tagline='$tagline', region=$region, profilePictureId=$profilePictureId)"
    }

    enum class Region(val descriptor: String) {
        NA("North America"),
        EUW("Europe West"),
        EUNE("Europe Northern-East"),
        KR("Korea"),
        BR("Brazil"),
        LAN("Latin America North"),
        LAS("Latin America South"),
        OCE("Oceania"),
        JP("Japan"),
        RU("Russia"),
        TR("Turkey")
    }
}