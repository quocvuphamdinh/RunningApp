package vu.pham.runningappseminar.viewmodels.viewmodelfactories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.viewmodels.RunDetailViewModel

class RunDetailViewModelFactory(private val mainRepository: MainRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RunDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RunDetailViewModel(mainRepository) as T
        }
        throw IllegalArgumentException("Unknown RunDetailViewModel class")
    }
}