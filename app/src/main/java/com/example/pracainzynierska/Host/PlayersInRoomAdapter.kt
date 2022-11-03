package com.example.pracainzynierska.Host


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.databinding.RoomPlayerItemBinding

import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class PlayersInRoomAdapter(options: FirebaseRecyclerOptions<Friends>): FirebaseRecyclerAdapter<Friends, PlayersInRoomAdapter.ViewHolder>(options) {
    private lateinit var binding: RoomPlayerItemBinding

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email: TextView = binding.PlayerInTable
        var kick: Button = binding.kick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        binding = RoomPlayerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        holder.email.text = model.email
        holder.kick.setOnClickListener {
            HostFragment.KickFromTable(it, model)
        }
    }
}