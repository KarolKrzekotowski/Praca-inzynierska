package com.example.pracainzynierska.Game
// data klasa żądania - kolor, nazwa, ilość kolejek do czekania
/**
 * data klasa żądań kart funkcyjnych
 *
 * @property colour kolor
 * @property type typ karty
 * @property freeze ilość kolejek do stania
 */
data class Order(
    var colour:String ?=null,
    var type:String?= null,
    var freeze:Int?=0)
