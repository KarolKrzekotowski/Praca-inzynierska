package com.example.pracainzynierska.Friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.R
import com.example.pracainzynierska.databinding.FriendsInvitationItemBinding
import com.example.pracainzynierska.databinding.FriendsItemListBinding

class FriendsInvitationAdapter :RecyclerView.Adapter<FriendsInvitationAdapter.ViewHolder>() {
    private lateinit var binding: FriendsInvitationItemBinding
    private var friendsInvitationList = mutableListOf<Friends>()

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var email: TextView = binding.friendEmail
        var accept:Button = binding.acceptToFriends
        var reject:Button = binding.Reject
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendsInvitationAdapter.ViewHolder {
        binding = FriendsInvitationItemBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )
        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return friendsInvitationList.size
    }

//    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
//        val friend = friendsList[position]
//        val email = holder.email
//        email.text = friend.email
//        val button: Button = binding.deleteFromFriends
//        button.setOnClickListener {
//            TODO("remove from friends")
//        }
//    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friendsInvitationList[position]
        val email = holder.email
        email.text = friend.email

    holder.accept.setOnClickListener {
        TODO("accept")
    }
    holder.reject.setOnClickListener {
        TODO("reject")
    }
    }
    fun rejectFriend(position: Int){
        friendsInvitationList.remove(getFriend(position))
    }
    fun getFriend(position: Int):Friends{
        return friendsInvitationList[position]
    }
    fun deleteFriend(position: Int) {
        friendsInvitationList.remove(getFriend(position))
        notifyDataSetChanged()
    }
    fun setData(friends: MutableList<Friends>){
        this.friendsInvitationList = friends
//        this.agregationList = aggregated

        notifyDataSetChanged()
    }
}