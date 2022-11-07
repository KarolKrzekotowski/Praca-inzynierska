package com.example.pracainzynierska.Game

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Cards.Cards
import com.example.pracainzynierska.databinding.MyCardsItemBinding
import kotlinx.android.synthetic.main.my_cards_item.view.*

class MyCardsAdapter: RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {


    private lateinit var binding:MyCardsItemBinding
    private val deck = mutableListOf<Cards>()
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var handCard: ImageView = binding.handCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MyCardsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = deck[position]
        holder.handCard.setImageBitmap(deck[position].image)
    }

    override fun getItemCount(): Int {
        return deck.size
    }
}