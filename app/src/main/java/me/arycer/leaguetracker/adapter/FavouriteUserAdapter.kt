package me.arycer.leaguetracker.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.arycer.leaguetracker.R
import me.arycer.leaguetracker.api.RetrofitInstance
import me.arycer.leaguetracker.api.model.UserProfile
import me.arycer.leaguetracker.model.FavouriteUser

class FavouriteUserAdapter(
    private val favouriteUsers: MutableList<FavouriteUser>,
    private val onDeleteUser: (Int) -> Unit
) : RecyclerView.Adapter<FavouriteUserAdapter.FavouriteUserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_fav_user, parent, false)
        return FavouriteUserViewHolder(itemView)
    }

    override fun getItemCount(): Int = favouriteUsers.size

    override fun onBindViewHolder(holder: FavouriteUserViewHolder, position: Int) {
        val favouriteUser = favouriteUsers[position]
        val context = holder.itemView.context

        fetchUserProfile(favouriteUser, holder, context)

        holder.deleteButton.setOnClickListener {
            onDeleteUser(position)
        }
    }

    private fun fetchUserProfile(user: FavouriteUser, holder: FavouriteUserViewHolder, context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getProfile(user.name, user.tagline)
                updateUserView(holder, context, user, response.body())
            } catch (exception: Exception) {
                Log.e("FavouriteUserAdapter", "Error fetching profile", exception)
            }
        }
    }

    private suspend fun updateUserView(
        holder: FavouriteUserViewHolder,
        context: Context,
        user: FavouriteUser,
        profile: UserProfile?
    ) = withContext(Dispatchers.Main) {
        holder.nameTextView.text = context.getString(
            R.string.user_name_tagline,
            profile?.username ?: user.name,
            profile?.tagline ?: user.tagline
        )

        holder.regionTextView.text = context.getString(
            R.string.region_format,
            user.region.descriptor
        )

        holder.levelTextView.text = context.getString(
            R.string.level_format,
            profile?.level ?: 0
        )

        holder.rankTextView.text = context.getString(
            R.string.league_format,
            profile?.soloRankedInfo?.tier ?: "Unranked",
            profile?.soloRankedInfo?.rank ?: ""
        )

        holder.lpTextView.text = context.getString(
            R.string.lps_format,
            profile?.soloRankedInfo?.leaguePoints ?: 0
        )

        holder.winrateTextView.text = context.getString(
            R.string.winrate_format,
            profile?.soloRankedInfo?.wins ?: 0,
            profile?.soloRankedInfo?.losses ?: 0
        )

        val profileImageUrl = buildProfileImageUrl(profile?.profileIconId ?: 1)
        loadImage(holder.imageView, profileImageUrl)
    }

    private fun buildProfileImageUrl(profileIconId: Int): String {
        return "https://ddragon.leagueoflegends.com/cdn/14.23.1/img/profileicon/$profileIconId.png"
    }

    private fun loadImage(imageView: ImageView, imageUrl: String) {
        Glide.with(imageView.context)
            .load(imageUrl)
            .placeholder(R.drawable.default_profile_picture)
            .error(R.drawable.default_profile_picture)
            .into(imageView)
    }

    class FavouriteUserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.player_name_text)
        val regionTextView: TextView = itemView.findViewById(R.id.region_text)
        val levelTextView: TextView = itemView.findViewById(R.id.level_text)
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
        val rankTextView: TextView = itemView.findViewById(R.id.rank_tier_text)
        val lpTextView: TextView = itemView.findViewById(R.id.lps_text)
        val winrateTextView: TextView = itemView.findViewById(R.id.winrate_text)
    }
}