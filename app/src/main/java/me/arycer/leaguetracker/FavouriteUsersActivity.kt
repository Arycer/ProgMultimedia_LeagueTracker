package me.arycer.leaguetracker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.arycer.leaguetracker.adapter.FavouriteUserAdapter
import me.arycer.leaguetracker.model.FavouriteUser

class FavouriteUsersActivity : AppCompatActivity() {
    private lateinit var userAdapter: FavouriteUserAdapter
    private lateinit var recyclerView: RecyclerView
    private val users = mutableListOf<FavouriteUser>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favourite_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        this.recyclerView = findViewById(R.id.recyclerView)
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        this.users.addAll(generateSampleUsers())

        this.userAdapter = FavouriteUserAdapter(this.users) { position ->
            this.users.removeAt(position)
            this.userAdapter.notifyItemRemoved(position)
            this.userAdapter.notifyItemRangeChanged(position, this.users.size)
        }

        this.recyclerView.adapter = this.userAdapter
    }

    private fun generateSampleUsers(): Collection<FavouriteUser> {
        return listOf(
            FavouriteUser("Arycer", "0000", FavouriteUser.Region.EUW, 1),
            FavouriteUser("I HATE JINX", "0000", FavouriteUser.Region.EUW, 2),
            FavouriteUser("Nightess", "0000", FavouriteUser.Region.EUW, 3),
            FavouriteUser("Arturisimoooo", "0000", FavouriteUser.Region.EUW, 4),
            FavouriteUser("ToukaLover", "0000", FavouriteUser.Region.EUW, 5),
            FavouriteUser("AleIV", "0000", FavouriteUser.Region.EUW, 6),
            FavouriteUser("PaulleXd", "0000", FavouriteUser.Region.EUW, 7),
            FavouriteUser("MitosV", "0000", FavouriteUser.Region.EUW, 8),
            FavouriteUser("Suuitt", "0000", FavouriteUser.Region.EUW, 9),
            FavouriteUser("CelesteLove", "0000", FavouriteUser.Region.EUW, 10)
        )
    }
}

