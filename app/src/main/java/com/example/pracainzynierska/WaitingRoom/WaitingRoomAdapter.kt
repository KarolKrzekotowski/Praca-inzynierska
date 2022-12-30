package com.example.pracainzynierska.WaitingRoom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Host.HostFragment
import com.example.pracainzynierska.Host.ReadyToInviteAdapter
import com.example.pracainzynierska.databinding.RoomPlayerToInviteItemBinding
import com.example.pracainzynierska.databinding.RoomWaitingItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

/**
 * Adapter RecyclerView poczekalni dla gości
 *
 * @param options zapytanie do Firebase o dane do wyświetlenia w RecyclerView
 */
class WaitingRoomAdapter(options: FirebaseRecyclerOptions<Friends>) : FirebaseRecyclerAdapter<Friends, WaitingRoomAdapter.ViewHolder>(options) {
    private lateinit var itemBinding: RoomWaitingItemBinding

    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaitingRoomAdapter.ViewHolder {
        itemBinding = RoomWaitingItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(itemBinding.root)
    }

    /**
     *
     * @param itemView email do dodania do ViewHolder
     */
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email: TextView = itemBinding.emailOfOtherPlayers

    }

    /**
     * Wyświetla email osób w poczekalni na określonych pozycjach
     *
     * @see RecyclerView.Adapter.onBindViewHolder
     *
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        holder.email.text = model.email

    }

}