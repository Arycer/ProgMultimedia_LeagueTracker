package me.arycer.leaguetracker

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.arycer.leaguetracker.adapter.FavouriteUserAdapter
import me.arycer.leaguetracker.dialog.EditProfileDialog
import me.arycer.leaguetracker.model.FavouriteProfile

class FavouriteUsersActivity : AppCompatActivity() {
    private lateinit var userAdapter: FavouriteUserAdapter
    private lateinit var recyclerView: RecyclerView
    private val users = mutableListOf<FavouriteProfile>()

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

        this.userAdapter = FavouriteUserAdapter(this.users, {
            this.showEditDialog(it)
        }, { position ->
            this.users.removeAt(position)
            this.userAdapter.notifyItemRemoved(position)
            this.userAdapter.notifyItemRangeChanged(position, this.users.size)
        })

        this.recyclerView.adapter = this.userAdapter

        findViewById<View>(R.id.add_button).setOnClickListener {
            this.showEditDialog(null)
        }
    }

    private fun showEditDialog(position: Int?) {
        val editDialog: EditProfileDialog
        if (position != null) {
            val profile: FavouriteProfile = users[position]
            editDialog = EditProfileDialog(this, profile) { updatedUser ->
                users[position] = updatedUser
                userAdapter.notifyItemChanged(position)
            }
        } else {
            editDialog = EditProfileDialog(this, null) { newUser ->
                users.add(newUser)
                userAdapter.notifyItemInserted(users.size - 1)
            }
        }

        editDialog.show()
    }

    private fun generateSampleUsers(): Collection<FavouriteProfile> {
        return listOf(
            FavouriteProfile("Arycer", "5190", FavouriteProfile.Region.EUW),
            FavouriteProfile("T0uk4", "L0v3R", FavouriteProfile.Region.EUW),
            FavouriteProfile("Likantros", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("Arturisimoooo", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("ToukaLover", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("AleIV", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("PaulleXd", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("MitosV", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("Suuitt", "0000", FavouriteProfile.Region.EUW),
            FavouriteProfile("CelesteLove", "0000", FavouriteProfile.Region.EUW)
        )
    }
}

