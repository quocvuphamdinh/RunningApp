package vu.pham.runningappseminar.utils

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import vu.pham.runningappseminar.database.remote.RetrofitBuilder
import vu.pham.runningappseminar.database.local.RunningDatabase
import vu.pham.runningappseminar.repositories.MainRepository

class RunApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { RunningDatabase.getInstance(this) }
    val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE) }
    val storage by lazy {Firebase.storage(Constants.URL_FIREBASE)}
    val repository by lazy { MainRepository(database.getRunDAO(), RetrofitBuilder.API_SERVICE, sharedPreferences, storage) }
}