package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Music
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.models.UserActivity
import vu.pham.runningappseminar.models.Workout
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.services.Polyline
import vu.pham.runningappseminar.utils.DataStore

class ExerciseRunViewModel(private val mainRepository: MainRepository) : ViewModel() {

    var currentTimeInMillies=0L

    var isTracking = false
    var pathPoints = mutableListOf<Polyline>()

    var weight:Float = 80f
    var distanceInMeters = 0
    var averageSpeed = 0F
    var caloriesBurned = 0
    var workours = ArrayList<Workout>()
    var lastCurrentTime = 0L
    private var _index :MutableLiveData<Int> = MutableLiveData<Int>(0)
    val index : LiveData<Int>
    get() = _index

    private var _userActivity : MutableLiveData<UserActivity> = MutableLiveData(UserActivity())
    val userActivity : LiveData<UserActivity>
    get() = _userActivity

    private var _toastEvent : MutableLiveData<String> = MutableLiveData()
    val toastEvent : LiveData<String>
    get() = _toastEvent

    private var _success : MutableLiveData<Boolean> = MutableLiveData(false)
    val success : LiveData<Boolean>
    get() = _success

    private var _music: MutableLiveData<Music> = MutableLiveData()
    val music: LiveData<Music>
    get() = _music

    private var _musicList: MutableLiveData<List<Music>> = MutableLiveData(DataStore.getListMusicLocal())
    val musicList: LiveData<List<Music>>
        get() = _musicList

    var positionMusic = 0

    fun initMusicIsPlaying(){
        _musicList.value!![positionMusic].isPlaying = true
        _musicList.postValue(_musicList.value)
    }
    fun goNextMusic(position:Int){
        for (i in 0 until _musicList.value!!.size){
            _musicList.value!![i].isPlaying = false
        }
        _musicList.value!![position].isPlaying = true
        _musicList.postValue(_musicList.value)
        _music.postValue(DataStore.getListMusicLocal()[position])
    }
    fun getMusicPosition(music: Music):Int{
        return _musicList.value!!.indexOf(music)
    }

    fun updateIndex(){
        _index.postValue(_index.value?.plus(1) ?: 0)
    }
    fun resetData(){
        currentTimeInMillies = 0L
        distanceInMeters = 0
        averageSpeed = 0F
        caloriesBurned = 0
        lastCurrentTime = 0L
        positionMusic = 0
        _index.postValue(0)
    }

    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()

    fun insertUserExercise(userActivity: UserActivity, userId: Long, run: Run) = viewModelScope.launch {
        try {
            mainRepository.insertRun(run)
            _userActivity.postValue(mainRepository.insertUserExercise(userActivity, userId))
        }catch (e : Exception){
            _userActivity.postValue(UserActivity())
            _toastEvent.postValue("An error has occurred, something happens in server !")
        }
    }
    fun insertRunLocal(run: Run) = viewModelScope.launch {
        try {
            mainRepository.insertRun(run)
            _success.postValue(true)
        }catch (e : Exception){
            _toastEvent.postValue("An error has occurred in your device !")
            _success.postValue(false)
        }
    }
}