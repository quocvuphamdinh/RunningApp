package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.DetailExerciseViewModel
import vu.pham.runningappseminar.viewmodels.EditProfileViewModel
import vu.pham.runningappseminar.viewmodels.ListExerciseViewModel

class ListExerciseViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListExerciseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListExerciseViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown ListExerciseViewModel class")
    }
}