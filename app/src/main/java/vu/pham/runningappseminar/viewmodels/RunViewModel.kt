package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.repositories.MainRepository

class RunViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()

}