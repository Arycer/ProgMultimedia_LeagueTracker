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
import kotlinx.coroutines.Job
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

    private val profileCache: MutableMap<String, UserProfile> = mutableMapOf()
    private val jobMap: MutableMap<Int, Job> = mutableMapOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavouriteUserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_fav_user, parent, false)
        return FavouriteUserViewHolder(itemView)
    }

    override fun getItemCount(): Int = favouriteUsers.size

    override fun onBindViewHolder(holder: FavouriteUserViewHolder, position: Int) {
        val favouriteUser = favouriteUsers[position]
        val context = holder.itemView.context
        val userKey = "${favouriteUser.name}#${favouriteUser.tagline}"

        jobMap[position]?.cancel()

        if (profileCache.containsKey(userKey)) {
            updateUserView(holder, context, favouriteUser, profileCache[userKey])
        } else {
            showLoadingState(holder)
            val job = CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitInstance.api.getProfile(
                        favouriteUser.region.name.lowercase(),
                        favouriteUser.name,
                        favouriteUser.tagline
                    )
                    val profile = response.body()
                    profile?.let {
                        profileCache[userKey] = it
                    }

                    withContext(Dispatchers.Main) {
                        if (holder.adapterPosition == position) {
                            updateUserView(holder, context, favouriteUser, profile)
                        }
                    }
                } catch (exception: Exception) {
                    Log.e("FavouriteUserAdapter", "Error fetching profile", exception)
                }
            }

            jobMap[position] = job
        }

        holder.deleteButton.setOnClickListener {
            onDeleteUser(position)
        }
    }

    override fun onViewRecycled(holder: FavouriteUserViewHolder) {
        super.onViewRecycled(holder)
        jobMap[holder.adapterPosition]?.cancel()
    }

    private fun showLoadingState(holder: FavouriteUserViewHolder) {
        holder.nameTextView.text = holder.itemView.context.getString(R.string.profile_loading)
        holder.regionTextView.text = ""
        holder.levelTextView.text = ""
        holder.rankTextView.text = ""
        holder.lpTextView.text = ""
        holder.winrateTextView.text = ""
        holder.imageView.setImageResource(R.drawable.default_profile_picture)
    }

    private fun updateUserView(
        holder: FavouriteUserViewHolder,
        context: Context,
        user: FavouriteUser,
        profile: UserProfile?
    ) {
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

        var winRatePercentage = 0f
        profile?.let {
            val totalGames = profile.soloRankedInfo?.wins?.plus(profile.soloRankedInfo.losses) ?: 0
            winRatePercentage = if (totalGames > 0) {
                (profile.soloRankedInfo?.wins?.toFloat() ?: 0f) / totalGames * 100
            } else {
                0f
            }
        }

        holder.winrateTextView.text = context.getString(
            R.string.winrate_format,
            profile?.soloRankedInfo?.wins ?: 0,
            profile?.soloRankedInfo?.losses ?: 0,
            "%.2f".format(winRatePercentage)
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