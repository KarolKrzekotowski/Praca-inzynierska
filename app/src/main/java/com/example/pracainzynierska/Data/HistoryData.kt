package com.example.pracainzynierska.Data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.Date

@Entity(tableName = "historia")
@Parcelize
data class HistoryData (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "pozycja") var position:Int=0,
    @ColumnInfo(name = "data") var date: String = LocalDateTime.now().toString() ,
    @ColumnInfo(name = "ilość graczy") var NumberOfPlayers: Int = 0
    ):Parcelable