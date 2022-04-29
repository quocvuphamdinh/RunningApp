package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ListRecentExerciseViewModel

class ListRecentExerciseViewModelFactory(private val mainRepository: MainRepository, private val app:RunApplication) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListRecentExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListRecentExerciseViewModel(mainRepository, app) as T
        }
        throw IllegalArgumentException("Unknown ListRecentExerciseViewModel class")
    }
}