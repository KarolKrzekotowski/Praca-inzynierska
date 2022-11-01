package com.example.pracainzynierska.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDataDAO {
    @Query("Select * from historia order by data ")
    fun showHistory(): LiveData<List<HistoryData>>

    @Insert
    fun insertToDatabase(vararg history: HistoryData)
}