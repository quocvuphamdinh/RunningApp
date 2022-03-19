package vu.pham.runningappseminar.viewmodels

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.database.Run
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.SortType
import java.time.LocalDate

class MainViewModel(private val mainRepository: MainRepository) : ParentViewModel(mainRepository) {

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


    var userLiveData = MutableLiveData<User?>()
    fun getUserLiveData(username: String, password: String){
        mainRepository.getUser(username, password).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                userLiveData.value = response.body()
                Log.d("call api", response.body().toString())
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                userLiveData.value = null
                Log.d("call api error", t.toString())
            }
        })
    }


    val totalDistanceWeekly = mainRepository.getTotalDitanceWeekly()

    val totalCaloriesBurnedToday = mainRepository.getTotalCaloriesBurnedToDay()

    val totalTimeInMilliesToday = mainRepository.getTotalTimeInMillisToday()

    val totalAvgSpeedToday = mainRepository.getTotalAvgSpeedInKMHToday()

    val runCount = mainRepository.getCountRunToday()

    val maxDistance = mainRepository.getMaxDistance()

    val maxTimeInMillies = mainRepository.getMaxTimeInMillies()

    val maxCaloriesBurned = mainRepository.getMaxCaloriesBurned()

    val maxAvgSpeed = mainRepository.getMaxAvgSpeedInKMH()

    val totalDistance = mainRepository.getTotalDistance()

    val totalTimeInMillies = mainRepository.getTotalTimeInMillies()

    val totalCaloriesBurned = mainRepository.getTotalCaloriesBurned()

    val totalAvgSpeed = mainRepository.getTotalAvgSpeedInKMH()

    fun insertRun(run: Run) = viewModelScope.launch {
        mainRepository.insertRun(run)
    }
}