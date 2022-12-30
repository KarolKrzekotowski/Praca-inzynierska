package com.example.pracainzynierska.Cards

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.Image
// data klasa karty
/**
 * data klasa karty
 *
 * @property colour kolor karty
 * @property type typ karty
 * @property image położenie obrazka karty
 */
data class Cards(
    val colour:String="",
    val type:String = "",
    val image: Int = 0

)
