package com.example.pracainzynierska.Friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.R
import com.example.pracainzynierska.databinding.FriendsItemListBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

/**
 * Adapter RecyclerView osób, które są naszymi znajomymi
 *
 * @param options zapytanie do bazy danych Firebase o dane do wyświetlenia
 */
class FriendsAdapter(options: FirebaseRecyclerOptions<Friends>): FirebaseRecyclerAdapter<Friends,FriendsAdapter.ViewHolder>(options) {
    private lateinit var binding: FriendsItemListBinding

    /**
     * RecyclerView ViewHolder na widoki znajomych i przycisków usuwania
     *
     * @param itemView
     */
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var email:TextView = binding.friendEmail
        val deleteButton:Button=binding.Delete
    }

    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = FriendsItemListBinding.inflate(
            LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    /**
     * Wyświetla email i przycisk do usuwania znajomego
     * @see onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        // nazwa znajomego umieszczona w widoku
        holder.email.text = model.email
        // usuwanie ze znajomych po kliknięciu usuń
        holder.deleteButton.setOnClickListener {
            FriendsFragment.DeleteFromFriends(it, model)
        }
    }

}