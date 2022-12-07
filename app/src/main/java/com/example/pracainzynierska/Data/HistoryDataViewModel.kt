package com.example.pracainzynierska.Data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryDataViewModel(application: Application): AndroidViewModel(application){
var allGamesHistory: LiveData<List<HistoryData>>
private val HistoryDataRepository:HistoryDataRepository

    init {
        val appDBDao = AppDatabase.getDatabase(
            application
        ).appDBDao()

        HistoryDataRepository = HistoryDataRepository(appDBDao)

        allGamesHistory = HistoryDataRepository.allGames
        //Log.e("12345676890", dayRepository.daysWithActivities.value.toString())
    }
    fun  insert(historyData: HistoryData) = viewModelScope.launch(Dispatchers.IO) {
        HistoryDataRepository.insert(historyData)
    }

}

class HistoryDataViewModelFactory(private val mApplication: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryDataViewModel(mApplication) as T
    }
}
