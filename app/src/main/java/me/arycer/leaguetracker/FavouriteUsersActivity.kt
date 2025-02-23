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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import me.arycer.leaguetracker.adapter.FavouriteUserAdapter
import me.arycer.leaguetracker.dialog.EditProfileDialog
import me.arycer.leaguetracker.model.FavouriteProfile

class FavouriteUsersActivity : AppCompatActivity() {
    private lateinit var userAdapter: FavouriteUserAdapter
    private lateinit var recyclerView: RecyclerView
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val users = mutableListOf<FavouriteProfile>()
    private var listenerRegistration: ListenerRegistration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favourite_users)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        userAdapter = FavouriteUserAdapter(users, { showEditDialog(it) }, { deleteUser(it) })
        recyclerView.adapter = userAdapter

        findViewById<View>(R.id.add_button).setOnClickListener {
            showEditDialog(null)
        }

        listenForUsers()
    }

    private fun listenForUsers() {
        listenerRegistration = firestore.collection("favouriteUsers")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "Error fetching users", e)
                    return@addSnapshotListener
                }
                if (snapshots == null) return@addSnapshotListener

                users.clear()
                for (doc in snapshots.documents) {
                    doc.toObject(FavouriteProfile::class.java)?.let { users.add(it) }
                }
                userAdapter.notifyDataSetChanged()
            }
    }

    private fun showEditDialog(position: Int?) {
        val editDialog: EditProfileDialog = if (position != null) {
            val profile = users[position]
            EditProfileDialog(this, profile) { updatedUser ->
                firestore.collection("favouriteUsers").document(profile.id).set(updatedUser)
            }
        } else {
            EditProfileDialog(this, null) { newUser ->
                firestore.collection("favouriteUsers")
                    .document(newUser.id)
                    .set(newUser)
            }
        }
        editDialog.show()
    }

    private fun deleteUser(position: Int) {
        firestore.collection("favouriteUsers").document(users[position].id).delete()
    }

    override fun onDestroy() {
        super.onDestroy()
        listenerRegistration?.remove()
    }
}
