package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.*
import vu.pham.runningappseminar.repositories.MainRepository

class AnalysisViewModel(private val mainRepository: MainRepository) : ViewModel() {

    fun getListDistanceInSpecificDate(date:String) = mainRepository.getListDistanceInSpecificDate(date)
}