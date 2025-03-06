package me.arycer.leaguetracker.fragment

import android.annotation.SuppressLint
import android.media.SoundPool
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import me.arycer.leaguetracker.R
import me.arycer.leaguetracker.adapter.FavouriteUserAdapter
import me.arycer.leaguetracker.dialog.EditProfileDialog
import me.arycer.leaguetracker.model.FavouriteProfile

class FavUsersFragment : Fragment(R.layout.fragment_fav_users) {

    private lateinit var userAdapter: FavouriteUserAdapter
    private lateinit var recyclerView: RecyclerView
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val users = mutableListOf<FavouriteProfile>()
    private var listenerRegistration: ListenerRegistration? = null
    private val soundPool = SoundPool.Builder().setMaxStreams(1).build()
    private var soundButtonId: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuración del RecyclerView y su adapter
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        userAdapter = FavouriteUserAdapter(users, { showEditDialog(it) }, { deleteUser(it) })
        recyclerView.adapter = userAdapter
        soundButtonId = soundPool.load(this.context, R.raw.click, 1)

        // Configura el botón para agregar un nuevo usuario
        view.findViewById<View>(R.id.add_button).setOnClickListener {
            showEditDialog(null)
            soundPool.play(soundButtonId, 1f, 1f, 1, 0, 1f)
        }

        // Inicia la escucha de cambios en Firestore
        listenForUsers()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundPool.release()
    }

    @SuppressLint("NotifyDataSetChanged")
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
        soundPool.play(soundButtonId, 1f, 1f, 1, 0, 1f)
        val editDialog = if (position != null) {
            val profile = users[position]
            EditProfileDialog(requireActivity(), profile) { updatedUser ->
                firestore.collection("favouriteUsers")
                    .document(profile.id)
                    .set(updatedUser)
                Toast.makeText(requireContext(), "Usuario actualizado", Toast.LENGTH_SHORT).show()
            }
        } else {
            EditProfileDialog(requireActivity(), null) { newUser ->
                firestore.collection("favouriteUsers")
                    .document(newUser.id)
                    .set(newUser)
                Toast.makeText(requireContext(), "Usuario agregado", Toast.LENGTH_SHORT).show()
            }
        }
        editDialog.show()
    }

    private fun deleteUser(position: Int) {
        soundPool.play(soundButtonId, 1f, 1f, 1, 0, 1f)
        firestore.collection("favouriteUsers")
            .document(users[position].id)
            .delete()
        Toast.makeText(requireContext(), "Usuario eliminado", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        listenerRegistration?.remove()
    }
}