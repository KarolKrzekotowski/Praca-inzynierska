package com.example.pracainzynierska.Data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow

// Repozytorium
/**
 * Rpozytorium danych o historii rozgrywek
 *
 * @property HistoryDataDao interface z kwerendami sql
 */
class HistoryDataRepository(private val HistoryDataDao:HistoryDataDAO) {
    val allGames: LiveData<List<HistoryData>> = HistoryDataDao.showHistory()

    /**
     * Funkcja umieszcza w bazie danych informacje o rozgrywce
     *
     * @param historyData instancja data klasy z informacjami o rozgrywce
     */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(historyData: HistoryData){
        HistoryDataDao.insertToDatabase(historyData)
    }
}