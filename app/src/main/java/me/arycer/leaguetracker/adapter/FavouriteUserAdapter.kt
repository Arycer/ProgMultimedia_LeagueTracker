package me.arycer.leaguetracker.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import me.arycer.leaguetracker.R
import me.arycer.leaguetracker.model.FavouriteUser

class FavouriteUserAdapter(
    private val users: MutableList<FavouriteUser>,
    private val onDeleteClicked: (Int) -> Unit
) : RecyclerView.Adapter<FavouriteUserAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_fav_user, parent, false)
        return UserViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = users[position]

        val context = holder.itemView.context
        holder.nameTextView.text = context.getString(R.string.user_name_tagline, currentUser.name, currentUser.tagline)
        holder.regionTextView.text = currentUser.region.descriptor


        val profileImageUrl = "https://ddragon.leagueoflegends.com/cdn/14.23.1/img/profileicon/${currentUser.profilePictureId}.png"
        Log.d("FavouriteUserAdapter", "Profile image URL: $profileImageUrl")

        Glide.with(holder.itemView.context)
            .load(profileImageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e("FavouriteUserAdapter", "Failed to load image", e)
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: com.bumptech.glide.request.target.Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d("FavouriteUserAdapter", "Image loaded successfully")
                    return false
                }
            })
            .placeholder(R.drawable.default_profile_picture)
            .error(R.drawable.default_profile_picture)
            .into(holder.imageView)

        holder.deleteButton.setOnClickListener {
            onDeleteClicked(position)
        }
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.player_name_text)
        val regionTextView: TextView = itemView.findViewById(R.id.region_text)
        val imageView: ImageView = itemView.findViewById(R.id.profile_image)
        val deleteButton: Button = itemView.findViewById(R.id.delete_button)
    }
}