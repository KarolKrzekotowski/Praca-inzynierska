package com.example.pracainzynierska.Cards

import android.util.Log
import com.example.pracainzynierska.Game.GameFragment
import com.example.pracainzynierska.Game.Player
// data klasa kupki kart
/**
 * data klasa zbioru kart
 *
 * @property zbiór kart
 */
data class Deck( var deck:MutableList<Cards>){
    // dodawanie karty do talii
    /**
     * Funkcja dodająca karte do zbioru
     *
     * @param cards karta
     */
    fun add(cards: Cards){
        this.deck.add(cards)
    }

    /**
     * Funkcja kopiująca zbiór kart
     *
     * @return kopia zbioru kart
     */
    fun copyDeck():MutableList<Cards>{

        val newDeck = this.deck.toMutableList()
        return newDeck

    }
    private fun <T> shuffleDeck(list: MutableList<T>) {
        list.shuffle()
    }

    // tasowanie talii
    /**
     * Funkcja tasująca zbiór kart
     *
     */
    fun Shuffle(){
        shuffleDeck(deck)
    }
    // pobieranie 1 karty
    /**
     * Funkcja usuwa ze zbioru kart jedną kartę
     *
     * @return zwraca usuniętą kartę
     */
    fun TakeOneCard():Cards{
        val card = this.deck.get(0)
        this.deck.removeAt(0)
        return card
    }

    /**
     * Funkcja zwraca rozmiar zbioru kart
     *
     * @return rozmiar zbioru kart
     */
    fun GetDeckSize():Int{
        return this.deck.size
    }
    // funkjca rozdająca pierwsze 5 kart każdemu z graczy
    /**
     * Funkcja rozdaje każdemu graczowi 5 kart
     *
     * @param players lista graczy
     */
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
