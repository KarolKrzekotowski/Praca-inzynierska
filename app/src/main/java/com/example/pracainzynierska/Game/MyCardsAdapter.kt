package com.example.pracainzynierska.Game

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.pracainzynierska.Cards.Cards
import com.example.pracainzynierska.databinding.MyCardsItemBinding

import android.content.res.Resources
import android.graphics.Bitmap
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.graphics.scale
import com.example.pracainzynierska.R


class MyCardsAdapter: RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {

    private lateinit var context:Context
    private lateinit var binding:MyCardsItemBinding
    private var deck = mutableListOf<Cards>()
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var handCard: ImageView = binding.handCard
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MyCardsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = binding.root.context
        return ViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = deck[position]
        val bitmapfactory = BitmapFactory.decodeResource(context.resources,card.image)
        val high = context.resources.displayMetrics.heightPixels *0.25
        val width = context.resources.displayMetrics.widthPixels * 0.1
        val bitmap = Bitmap.createScaledBitmap(bitmapfactory,width.toInt(),high.toInt(),false)
        holder.handCard.setImageBitmap(bitmap)
        holder.handCard.setBackgroundResource(R.drawable.card_not_chosen)
        holder.handCard.setOnClickListener {
            GameFragment.chooseCard(it, card)
        }
    }

    override fun getItemCount(): Int {
        return deck.size
    }
    fun getCard(position: Int): Cards {
        return deck[position]
    }

    fun deleteCard(position: Int) {
        deck.remove(getCard(position))
        notifyDataSetChanged()
    }

    fun setData(cards: MutableList<Cards>){
        this.deck = cards
        notifyDataSetChanged()
    }

}