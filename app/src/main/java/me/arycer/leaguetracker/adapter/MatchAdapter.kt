package me.arycer.leaguetracker.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.arycer.leaguetracker.R

class MatchAdapter(
    private val matches: MutableList<String>,
    private val onMatchDetails: (Int) -> Unit
) : RecyclerView.Adapter<MatchAdapter.MatchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MatchViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_fav_user, parent, false)
        return MatchViewHolder(itemView)
    }

    override fun getItemCount(): Int = matches.size

    override fun onBindViewHolder(holder: MatchViewHolder, position: Int) {
        val matchId = matches[position]
        val context = holder.itemView.context

        holder.idTextView.text = matchId



        holder.itemView.setOnClickListener {
            onMatchDetails(position)
        }
    }

    class MatchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val idTextView: TextView = itemView.findViewById(R.id.player_name_text)
    }
}