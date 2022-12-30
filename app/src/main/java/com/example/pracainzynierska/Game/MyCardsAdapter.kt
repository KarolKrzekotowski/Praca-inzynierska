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

/**
 * Adapter RecyclerView do wyświetlania kart gracza
 *
 */
class MyCardsAdapter: RecyclerView.Adapter<MyCardsAdapter.ViewHolder>() {

    private lateinit var context:Context
    private lateinit var binding:MyCardsItemBinding
    private var deck = mutableListOf<Cards>()

    /**
     *RecyclerView Holder na obrazki kart
     * @param itemView obraz karty do dodania do Viewholder
     */
    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView) {
        var handCard: ImageView = binding.handCard

    }

    /**
     * @see RecyclerView.Adapter.onCreateViewHolder
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        binding = MyCardsItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        context = binding.root.context
        return ViewHolder(binding.root)
    }

    /**
     * Wyświetla karty gracza
     * @see RecyclerView.Adapter.onBindViewHolder
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val card = deck[position]
        // zmiana rozmiarów karty
        val bitmapfactory = BitmapFactory.decodeResource(context.resources,card.image)
        val high = context.resources.displayMetrics.heightPixels *0.25
        val width = context.resources.displayMetrics.widthPixels * 0.1
        val bitmap = Bitmap.createScaledBitmap(bitmapfactory,width.toInt(),high.toInt(),false)
        // ustawienie karty
        holder.handCard.setImageBitmap(bitmap)
        holder.handCard.setBackgroundResource(R.drawable.card_not_chosen)
        // funkcja wykonywana w momencie kliknięcia w kartę
        holder.handCard.setOnClickListener {
            GameFragment.chooseCard(it, card)
        }
    }

    /**
     *
     * @see RecyclerView.Adapter.getItemCount
     */
    override fun getItemCount(): Int {
        return deck.size
    }

    /**
     * Funkcja zwraca kartę z listy deck w zależności od pozycji
     *
     * @param position pozycja na liście kart deck
     *
     * @return Karta z listy deck
     */
    fun getCard(position: Int): Cards {
        return deck[position]
    }

    /**
     * funkcja usuwa daną kartę na danej pozycji z listy deck
     *
     * @param position pozcyja karty do usunięcie
     */
    fun deleteCard(position: Int) {
        deck.remove(getCard(position))
        notifyDataSetChanged()
    }

    /**
     * Funkcja ustawia listę kart deck
     *
     * @param cards lista kart typ Cards
     */
    fun setData(cards: MutableList<Cards>){
        this.deck = cards
        notifyDataSetChanged()
    }

}