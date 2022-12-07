package com.example.pracainzynierska.Cards

import android.util.Log
import com.example.pracainzynierska.Game.GameFragment
import com.example.pracainzynierska.Game.Player

data class Deck( var deck:MutableList<Cards>){

    fun add(cards: Cards){
        this.deck.add(cards)
    }


    fun copyDeck():MutableList<Cards>{

        val newDeck = this.deck.toMutableList()
        return newDeck

    }
    private fun <T> shuffleDeck(list: MutableList<T>) {
        list.shuffle()
    }
    fun Shuffle(){
        shuffleDeck(deck)
    }
    fun TakeOneCard():Cards{
        val card = this.deck.get(0)
        this.deck.removeAt(0)
        return card
    }
    fun GetDeckSize():Int{
        return this.deck.size
    }
    fun DealTheCards(players:List<Player>){
        for (player in players){
            for (i in 1..5){
                var card = TakeOneCard()
                player.deck.add(card)
               GameFragment.table.child("Players").child(player.email).child("deck").child(card.colour+"_"+card.type).setValue(card.colour+"_"+card.type)

            }
        }
    }

}
