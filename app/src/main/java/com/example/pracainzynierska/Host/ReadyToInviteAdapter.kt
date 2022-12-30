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

/**
 * Adapter RecyclerView osób, które są naszymi znajomymi
 *
 */
class ReadyToInviteAdapter:RecyclerView.Adapter<ReadyToInviteAdapter.ViewHolder>() {
    private lateinit var binding: RoomPlayerToInviteItemBinding
    private  var ReadyToInvitelist  = mutableListOf<Friends>()

    /**
     *  RecyclerView Holder na dane o znajomych
     * @param itemView email i przycisk zaproszenia do dodania do ViewHolder
     */
    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email:TextView = binding.InvitePlayerToTable
        var invite:Button = binding.inviteToGame
    }

    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = RoomPlayerToInviteItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(binding.root)
    }

    /**
     * Wyświetla email osób znajomych i przycisk zaproszenia  na określonych pozycjach
     *
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.email.text = ReadyToInvitelist[position].email
        // zaproszenie znajomego do poczekalni
        holder.invite.setOnClickListener {
            HostFragment.InviteToGame(it, ReadyToInvitelist[position])
        }
    }

    /**
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return ReadyToInvitelist.size
    }

    /**
     * Ustawia dane do listy ReadyToInvitelist
     *
     * @param friends Lista z obiektami Friends
     */
    fun setData(friends: MutableList<Friends>){
        this.ReadyToInvitelist = friends
        notifyDataSetChanged()
    }

    /**
     * Funckja zwracająca obiekt Friends w zależności od pozycji
     *
     * @param position pozycja
     * @return Friends
     */
    fun getFriend(position: Int): Friends {
        return ReadyToInvitelist[position]
    }
}