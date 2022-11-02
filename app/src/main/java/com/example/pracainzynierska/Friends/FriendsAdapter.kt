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
import kotlinx.android.synthetic.main.friends_invitation_item.view.*
import kotlinx.android.synthetic.main.friends_invitation_item.view.friendEmail
import kotlinx.android.synthetic.main.friends_item_list.view.*

class FriendsAdapter(options: FirebaseRecyclerOptions<Friends>): FirebaseRecyclerAdapter<Friends,FriendsAdapter.ViewHolder>(options) {
    private lateinit var binding: FriendsItemListBinding

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var email:TextView = itemView.friendEmail
        val deleteButton:Button=itemView.Delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = FriendsItemListBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )


        return ViewHolder(binding.root)
    }




    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        holder.email.text = model.email
        holder.deleteButton.setOnClickListener {
            FriendsFragment.DeleteFromFriends(it, model)
        }
    }

}