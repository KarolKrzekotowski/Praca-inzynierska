package com.example.pracainzynierska.Friends

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.R
import com.example.pracainzynierska.databinding.FriendsItemListBinding

class FriendsAdapter(): RecyclerView.Adapter<FriendsAdapter.ViewHolder>() {
    private lateinit var binding: FriendsItemListBinding
    private var friendsList = mutableListOf<Friends>()
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var email:TextView = itemView.findViewById(R.id.email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = FriendsItemListBinding.inflate(
            LayoutInflater.from(parent.context),parent,false
        )


        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return friendsList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val friend = friendsList[position]
        val email = holder.email
        email.text = friend.email
        val button:Button = binding.Delete
        button.setOnClickListener {
            TODO("remove from friends")
        }
    }
    fun getFriend(position: Int): Friends {
        return friendsList[position]
    }

    fun deleteFriend(position: Int) {
        friendsList.remove(getFriend(position))
        notifyDataSetChanged()
    }
    fun setData(friends: MutableList<Friends>){
        this.friendsList = friends
//        this.agregationList = aggregated

        notifyDataSetChanged()
    }
}