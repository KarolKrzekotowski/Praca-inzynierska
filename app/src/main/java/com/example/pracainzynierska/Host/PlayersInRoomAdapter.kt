package com.example.pracainzynierska.Host


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Game.Player
import com.example.pracainzynierska.databinding.RoomPlayerItemBinding

import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

/**
 * Adapter RecyclerView osób w poczekalni dla gospodarza
 *
 */
class PlayersInRoomAdapter:RecyclerView.Adapter<PlayersInRoomAdapter.ViewHolder>()  {
    private lateinit var binding: RoomPlayerItemBinding
    private var playersList = mutableListOf<Friends>()

    /**
     *
     *
     * @param itemView eamil i przycisk usunięcia do dodania do ViewHolder
     */
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email: TextView = binding.PlayerInTable
        var kick: Button = binding.kick
    }
    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = RoomPlayerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    /**
     * Wyświetla email osób w poczekalni i przycisk na określonych pozycjach
     *
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.email.text = playersList[position].email
        // usunięcie znajomego z poczekalni
        holder.kick.setOnClickListener {
            HostFragment.KickFromTable(it, playersList[position])
        }
    }

    /**
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return playersList.size
    }

    /**
     * Funkcja ustawia dane w liście playerlist
     *
     * @param friends
     */
    fun setData(friends: MutableList<Friends>){
        this.playersList = friends
        notifyDataSetChanged()
    }
}