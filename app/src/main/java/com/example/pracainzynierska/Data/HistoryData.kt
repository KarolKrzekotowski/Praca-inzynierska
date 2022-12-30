package com.example.pracainzynierska.Data


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date
// wygląd tabelki w wewnętrznej bazie danych
/**
 * data klasa rozgrywki
 * wygląd tabelki "historia" w bazie danych
 *
 * @property id id
 * @property position miejsce
 * @property date data rozgrywki
 * @property NumberOfPlayers liczba graczy
 */
@Entity(tableName = "historia")
@Parcelize
data class HistoryData (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "pozycja") var position:Int=0,
    @ColumnInfo(name = "data") var date: String = LocalDateTime.now().toString() ,
    @ColumnInfo(name = "ilość graczy") var NumberOfPlayers: Int = 0
    ): Parcelable