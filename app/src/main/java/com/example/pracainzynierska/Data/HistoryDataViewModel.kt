package com.example.pracainzynierska.Data

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryDataViewModel(application: Application): AndroidViewModel(application){
var allGamesHistory: LiveData<List<HistoryData>>
private val HistoryDataRepository:HistoryDataRepository

    /**
     * inicjalizacja bazy danych
     */
    init {
        // łączenie z bazą danych
        val appDBDao = AppDatabase.getDatabase(
            application
        ).appDBDao()
        // instacja repozytorium
        HistoryDataRepository = HistoryDataRepository(appDBDao)
        // LiveData umożliwia obserwowanie obiektu
        // lista zawierająca historie rozgrywek
        allGamesHistory = HistoryDataRepository.allGames

    }
    // funkcja dodająca daną rozgrywkę do bazy danych
    /**
     * Funkcja dodaje rozgrywkę do wewnętrznej bazy danych
     *
     * @param historyData instancja, która ma zostać dodana do bazy danych
     * @return
     */
    fun  insert(historyData: HistoryData) = viewModelScope.launch(Dispatchers.IO) {
        HistoryDataRepository.insert(historyData)
    }

}
// fabryka pozwalająca utworzyć viewModel
/**
 * Fabryka pozwalająca utworzyć ViewModel
 *
 * @property mApplication kontekst
 */
class HistoryDataViewModelFactory(private val mApplication: Application) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return HistoryDataViewModel(mApplication) as T
    }
}
