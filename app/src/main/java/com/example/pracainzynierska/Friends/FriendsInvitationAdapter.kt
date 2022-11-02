package com.example.pracainzynierska.Friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.R
import com.example.pracainzynierska.databinding.FriendsInvitationItemBinding
import com.example.pracainzynierska.databinding.FriendsItemListBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.friends_invitation_item.view.*

class FriendsInvitationAdapter(options: FirebaseRecyclerOptions<Friends>) : FirebaseRecyclerAdapter<Friends,FriendsInvitationAdapter.ViewHolder>(options) {


    inner class ViewHolder(invitationView:View) : RecyclerView.ViewHolder(invitationView) {

        val acceptButton : Button = itemView.accept_to_friends
        val declineButton : Button = itemView.Reject
        val friend : TextView = itemView.friendEmail

    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friends_invitation_item,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {

        holder.friend.text = model.email
        holder.acceptButton.setOnClickListener {
            FriendsFragment.AddToFriends(it,model)

        }
        holder.declineButton.setOnClickListener {
            FriendsFragment.DeclineInvitation(it,model)
        }

    }

}