package me.arycer.leaguetracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.arycer.leaguetracker.adapter.MatchAdapter
import me.arycer.leaguetracker.api.RetrofitInstance
import me.arycer.leaguetracker.model.FavouriteProfile

class MatchesActivity : AppCompatActivity() {
    private lateinit var userAdapter: MatchAdapter
    private lateinit var recyclerView: RecyclerView
    private val matches = mutableListOf<String>()

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_matches)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val username = intent.getStringExtra("Username")
        val tagline = intent.getStringExtra("Tagline")
        val region = FavouriteProfile.Region.entries[intent.getIntExtra("Region", 0)]

        CoroutineScope(Dispatchers.IO).launch {
            if (username == null || tagline == null) {
                Log.e("MatchesActivity", "Username or tagline is null")
                return@launch
            }

            val response = RetrofitInstance.api.getMatches(region.name.lowercase(), username, tagline)
            val receivedMatches = response.body()

            if (receivedMatches == null) {
                Log.e("MatchesActivity", "Received matches is null")
                return@launch
            }

            matches.addAll(receivedMatches)
            withContext(Dispatchers.Main) {
                userAdapter.notifyDataSetChanged()
            }
        }

        this.recyclerView = findViewById(R.id.recyclerView)
        this.recyclerView.layoutManager = LinearLayoutManager(this)

        this.userAdapter = MatchAdapter(this.matches) {
            Log.d("MatchesActivity", "Match clicked: $it")
        }

        this.recyclerView.adapter = this.userAdapter
    }
}

