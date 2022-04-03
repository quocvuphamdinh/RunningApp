package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.repositories.MainRepository

class DetailExerciseViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getActivityDetail(id:Long) = mainRepository.getActivityDetail(id)
}