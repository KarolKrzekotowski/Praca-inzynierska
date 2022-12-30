package com.example.pracainzynierska.Data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

// zapytania dostępne w bazie danych
/**
 * Interface z funkcjami dostępnymi w bazie danych
 *
 */
@Dao
interface HistoryDataDAO {
    /**
     *
     *  Funkcja zwraca obiekt, ktróy można obserwować
     * @return obiekt LiveData z lista odbytych rozgrywek
     */
    @Query("Select * from historia order by data ")
    fun showHistory(): LiveData<List<HistoryData>>

    /**
     * Funkcja umieszcza informacje o rozgrywce do bazy danych
     *
     * @param history informacja do umieszczenia w bazie danych
     */
    @Insert
    fun insertToDatabase(vararg history: HistoryData)
}