package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.HistoryRunViewModel

// class dùng để giúp cho MainViewModel có thể truyền tham số mainRepository vào dc
class HistoryRunViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryRunViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryRunViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown HistoryRunViewModel class")
    }
}