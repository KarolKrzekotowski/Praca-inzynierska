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

/**
 * Adapter RecyclerView graczy w rozgrywce
 *
 */
class WhoseTurnAdapter: RecyclerView.Adapter<WhoseTurnAdapter.ViewHolder>() {
    private lateinit var binding: QueueAdapterItemBinding
    private var players = mutableListOf<Player>()
    var theName = ""

    /**
     * RecyclerView Holder na nazwy graczy
     * @param itemView
     */
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var email :TextView = binding.Order
    }

    /**
     *
     *@see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = QueueAdapterItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    /**
     * Wyświetla nazwy graczy na określonych pozycjach
     * i podświetla nazwę na żółto gdy jest kolej tego gracza
     *
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = players[position]
        holder.email.text = person.email
        // podświetlenie osoby, której jest kolej
        if(person.email == theName ){
            holder.itemView.setBackgroundColor(Color.parseColor("#FFFF00"));
        }
        else{

            holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    /**
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return players.size
    }

    /**
     * Funckja ustawia dane do listy players
     *
     * @param friends Lista graczy
     */
    fun setData(friends:MutableList<Player>){
        this.players = friends
        notifyDataSetChanged()
    }

    /**
     * Funckja ustawia zmienną theName
     *
     * @param name nazwa gracza do podświetlenia
     */
    fun changeColor(name:String){
        this.theName = name
        notifyDataSetChanged()
    }
}