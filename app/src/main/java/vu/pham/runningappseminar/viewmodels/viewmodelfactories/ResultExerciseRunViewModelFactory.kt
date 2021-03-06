package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.ResultExerciseRunViewModel

class ResultExerciseRunViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ResultExerciseRunViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ResultExerciseRunViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown ResultExerciseRunViewModel class")
    }
}