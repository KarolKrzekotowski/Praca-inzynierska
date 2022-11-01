package com.example.pracainzynierska.Data

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.flow.Flow


class HistoryDataRepository(private val HistoryDataDao:HistoryDataDAO) {
    val allGames: LiveData<List<HistoryData>> = HistoryDataDao.showHistory()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(historyData: HistoryData){
        HistoryDataDao.insertToDatabase(historyData)
    }
}