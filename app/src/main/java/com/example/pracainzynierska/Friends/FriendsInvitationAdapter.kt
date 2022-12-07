package com.example.pracainzynierska.Friends

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.databinding.FriendsInvitationItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


class FriendsInvitationAdapter(options: FirebaseRecyclerOptions<Friends>) :
    FirebaseRecyclerAdapter<Friends,FriendsInvitationAdapter.ViewHolder>(options) {

    inner class ViewHolder(val binding: FriendsInvitationItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int): ViewHolder {
        val binding = FriendsInvitationItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        with(holder){
            binding.friendEmail.text = model.email
            binding.Reject.setOnClickListener {
                FriendsFragment.DeclineInvitation(it,model)
            }
            binding.acceptToFriends.setOnClickListener {
                FriendsFragment.AddToFriends(it,model)

            }
        }
    }
}