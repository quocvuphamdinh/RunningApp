package vu.pham.runningappseminar.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.models.Music
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.repositories.MainRepository
import vu.pham.runningappseminar.utils.DataStore
import java.lang.Exception

class RunViewModel(private val mainRepository: MainRepository) : ViewModel() {

    private var _success : MutableLiveData<Boolean> = MutableLiveData()
    val success : LiveData<Boolean>
    get() = _success

    private var _toastEvent : MutableLiveData<String> = MutableLiveData()
    val toastEvent : LiveData<String>
    get() = _toastEvent

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

    fun getFirebaseStorage() = mainRepository.getFirebaseStorage()

    fun insertRun(run: Run) = viewModelScope.launch {
        try {
            mainRepository.insertRun(run)
            _toastEvent.postValue("Run saved successfully !!")
            _success.postValue(true)
        }catch (e:Exception){
            _toastEvent.postValue("Run saved failed !!")
            _success.postValue(false)
        }
    }
    fun getUserFromSharedPref() = mainRepository.getUserFromSharedPref()
}