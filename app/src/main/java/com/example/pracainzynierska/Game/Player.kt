package com.example.pracainzynierska.Game

import com.example.pracainzynierska.Cards.Cards
//data klasa gracza
/**
 * Data klasa opisująca gracza
 *
 * @property email email gracza
 * @property deck karty gracza
 * @property myTurn kolej gracza
 */
data class Player(
    var email:String = "",
    var deck:MutableList<Cards>,
    var myTurn:Boolean = false
)
