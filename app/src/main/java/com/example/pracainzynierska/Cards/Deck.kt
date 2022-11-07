package com.example.pracainzynierska.Cards

import com.example.pracainzynierska.Game.Player

data class Deck(var deck:MutableList<Cards>){
    init {
        Shuffle()
    }
    fun Shuffle(){
        deck.shuffled()
    }
    fun TakeOneCard():Cards{

        deck.removeAt(0)
        return deck.get(0)
    }
    fun GetDeckSize():Int{
        return deck.size
    }
    fun DealTheCards(players:List<Player>){
        for (player in players){
            for (i in 1..5){
                player.deck.add(TakeOneCard())
            }
        }
    }

}
