package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.ViewModel
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.SortType

class HistoryRunOnlyViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private val runSortedByDate = mainRepository.getAllRunsSortedByDate()
    private val runSortedByTimeInMillies = mainRepository.getAllRunsSortedByTimeInMillies()
    private val runSortedByAvgSpeedInKMH = mainRepository.getAllRunsSortedByAvgSpeedInKMH()
    private val runSortedByCaloriesBurned = mainRepository.getAllRunsSortedByCaloriesBurned()
    private val runSortedByDistance = mainRepository.getAllRunsSortedByDistance()

    val runs = MediatorLiveData<List<Run>>() // sử dụng này để merge các livedata (hiển thị khác nhau) lại với nhau

    var sortType = SortType.DATE

    init {
        runs.addSource(runSortedByDate){ result->
            result?.let {
                if(sortType == SortType.DATE){
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByTimeInMillies){ result->
            result?.let {
                if(sortType == SortType.RUNNING_TIME){
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByAvgSpeedInKMH){ result->
            result?.let {
                if(sortType == SortType.AVG_SPEED){
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByCaloriesBurned){ result->
            result?.let {
                if(sortType == SortType.CALORIES_BURNED){
                    runs.value = it
                }
            }
        }
        runs.addSource(runSortedByDistance){ result->
            result?.let {
                if(sortType == SortType.DISTANCE){
                    runs.value = it
                }
            }
        }
    }

    fun sortRuns(sortType: SortType) = when(sortType){
        SortType.DATE -> runSortedByDate.value?.let { runs.value = it }
        SortType.RUNNING_TIME -> runSortedByTimeInMillies.value?.let { runs.value = it }
        SortType.AVG_SPEED -> runSortedByAvgSpeedInKMH.value?.let { runs.value = it }
        SortType.CALORIES_BURNED -> runSortedByCaloriesBurned.value?.let { runs.value = it }
        SortType.DISTANCE -> runSortedByDistance.value?.let { runs.value = it }
    }.also {
        this.sortType = sortType
    }
}