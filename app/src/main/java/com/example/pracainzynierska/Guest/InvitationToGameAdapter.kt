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
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.join_the_game_item.view.*


class InvitationToGameAdapter(options: FirebaseRecyclerOptions<Friends>) : FirebaseRecyclerAdapter<Friends, InvitationToGameAdapter.ViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int): InvitationToGameAdapter.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.friends_invitation_item,parent,false))
    }

    inner class ViewHolder(invitationView: View) : RecyclerView.ViewHolder(invitationView) {

        val joinButton: Button = itemView.join
        val email:TextView = itemView.ownerEmail


    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Friends) {
        holder.email.text = model.email
        holder.joinButton.setOnClickListener {
            GuestFragment.joinTheGame(it,model)
        }
    }
//        holder.friend.text = model.email
//        holder.acceptButton.setOnClickListener {
//            FriendsFragment.AddToFriends(it,model)
//
//        }
//        holder.declineButton.setOnClickListener {
//            FriendsFragment.DeclineInvitation(it,model)
//        }
//    }
}