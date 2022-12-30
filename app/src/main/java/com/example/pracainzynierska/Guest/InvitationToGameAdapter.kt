package com.example.pracainzynierska.Guest

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.Friends.FriendsFragment

import com.example.pracainzynierska.R
import com.example.pracainzynierska.databinding.JoinTheGameItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


/**
 * Adapter RecyclerView zaproszeń do gier
 *
 * @param options zapytanie do Firebase o dane do wyświetlenia w RecyclerView
 */
class InvitationToGameAdapter(options: FirebaseRecyclerOptions<Friends>) : FirebaseRecyclerAdapter<Friends, InvitationToGameAdapter.ViewHolder>(options) {

    private lateinit var binding: JoinTheGameItemBinding

    /*
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): InvitationToGameAdapter.ViewHolder {
        binding = JoinTheGameItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    /**
     * RecyclerView Holder na dane o zaproszeniach
     * @param invitationView eamil i przycisk zaproszenia do dodania do ViewHolder
     */
    inner class ViewHolder(invitationView: View) : RecyclerView.ViewHolder(invitationView) {
        val joinButton: Button = binding.join
        val email:TextView = binding.ownerEmail
    }

    /**
     * Wyświetla email i przycisk dołączenia na określonych pozycjach
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        holder.email.text = model.email
        holder.joinButton.setOnClickListener {
            GuestFragment.joinTheGame(it,model)
        }
    }

}