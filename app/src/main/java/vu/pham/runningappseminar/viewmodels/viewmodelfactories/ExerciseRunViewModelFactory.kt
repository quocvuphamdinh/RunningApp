package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.ExerciseRunViewModel

class ExerciseRunViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ExerciseRunViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ExerciseRunViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown ExerciseRunViewModel class")
    }
}