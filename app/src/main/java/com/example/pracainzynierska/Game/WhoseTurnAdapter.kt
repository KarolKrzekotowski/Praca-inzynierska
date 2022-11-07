package com.example.pracainzynierska.Game

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.pracainzynierska.databinding.WhosePlayingItemBinding
import kotlinx.android.synthetic.main.whose_playing_item.view.*

class WhoseTurnAdapter():RecyclerView.Adapter<WhoseTurnAdapter.ViewHolder>() {
    private lateinit var binding:WhosePlayingItemBinding
    private var playersList = emptyList<Player>()

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var email:TextView = binding.whoseTurnEmail

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = WhosePlayingItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val player = playersList[position]
        val email = holder.email
        email.text = player.email
        if (player.myTurn == true){
            binding.root.setBackgroundColor(Color.parseColor("#ffff00")  )
        }
    }
    fun changeBackground(position: Int){
        if (playersList[position].myTurn == true){

            binding.root.whoseTurnEmail.setTextColor(Color.YELLOW)
        }
        else{
            binding.root.whoseTurnEmail.setTextColor(Color.BLACK)
        }

    }

    override fun getItemCount(): Int {
        return playersList.size
    }

    fun setData(data: List<Player>){
        this.playersList=data
        notifyDataSetChanged()
    }
    fun getData(position: Int):Player{
        return playersList[position]
    }
}