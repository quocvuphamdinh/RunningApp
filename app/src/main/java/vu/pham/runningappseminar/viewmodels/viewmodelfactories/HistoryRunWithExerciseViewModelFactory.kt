package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.HistoryRunWithExerciseViewModel

// class dùng để giúp cho MainViewModel có thể truyền tham số mainRepository vào dc
class HistoryRunWithExerciseViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryRunWithExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryRunWithExerciseViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown HistoryRunWithExerciseViewModel class")
    }
}