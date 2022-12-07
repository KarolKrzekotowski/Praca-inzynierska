package com.example.pracainzynierska.Host

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.databinding.RoomPlayerToInviteItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class ReadyToInviteAdapter:RecyclerView.Adapter<ReadyToInviteAdapter.ViewHolder>() {
    private lateinit var binding: RoomPlayerToInviteItemBinding
    private  var ReadyToInvitelist  = mutableListOf<Friends>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email:TextView = binding.InvitePlayerToTable
        var invite:Button = binding.inviteToGame
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RoomPlayerToInviteItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(binding.root)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.email.text = ReadyToInvitelist[position].email

        holder.invite.setOnClickListener {
            HostFragment.InviteToGame(it, ReadyToInvitelist[position])
        }
    }

    override fun getItemCount(): Int {
        return ReadyToInvitelist.size
    }

    fun setData(friends: MutableList<Friends>){
        this.ReadyToInvitelist = friends


        notifyDataSetChanged()
    }

    fun getFriend(position: Int): Friends {
        return ReadyToInvitelist[position]
    }
}