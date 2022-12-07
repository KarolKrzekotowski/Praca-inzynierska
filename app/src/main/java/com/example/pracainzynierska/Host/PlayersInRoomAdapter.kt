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

class PlayersInRoomAdapter:RecyclerView.Adapter<PlayersInRoomAdapter.ViewHolder>()  {
    private lateinit var binding: RoomPlayerItemBinding
    private var playersList = mutableListOf<Friends>()
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email: TextView = binding.PlayerInTable
        var kick: Button = binding.kick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = RoomPlayerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.email.text = playersList[position].email
        holder.kick.setOnClickListener {
            HostFragment.KickFromTable(it, playersList[position])
        }
    }

    override fun getItemCount(): Int {
        return playersList.size
    }
    fun setData(friends: MutableList<Friends>){
        this.playersList = friends
        notifyDataSetChanged()
    }
}