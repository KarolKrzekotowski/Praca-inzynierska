package com.example.pracainzynierska.Game

import com.example.pracainzynierska.Cards.Cards

data class Player(
    var email:String = "",
    var deck:MutableList<Cards>,
    var myTurn:Boolean = false
)
