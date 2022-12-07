package com.example.pracainzynierska.Game

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Friends.Friends
import com.example.pracainzynierska.databinding.QueueAdapterItemBinding

class WhoseTurnAdapter: RecyclerView.Adapter<WhoseTurnAdapter.ViewHolder>() {
    private lateinit var binding: QueueAdapterItemBinding
    private var players = mutableListOf<Player>()
    var theName = ""
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var email :TextView = binding.Order
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = QueueAdapterItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = players[position]
        holder.email.text = person.email

        if(person.email == theName ){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFF00"));
        }
        else{
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    override fun getItemCount(): Int {
        return players.size
    }

    fun setData(friends:MutableList<Player>){
        this.players = friends
        notifyDataSetChanged()
    }
    fun changeColor(name:String){
        this.theName = name
        notifyDataSetChanged()
    }
}