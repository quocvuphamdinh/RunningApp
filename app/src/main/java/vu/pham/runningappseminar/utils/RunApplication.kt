package vu.pham.runningappseminar.utils

import android.app.Application
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vu.pham.runningappseminar.database.FirebaseRun
import vu.pham.runningappseminar.database.RetrofitBuilder
import vu.pham.runningappseminar.database.RunningDatabase
import vu.pham.runningappseminar.repositories.MainRepository

class RunApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { RunningDatabase.getInstance(this) }
    val repository by lazy { MainRepository(database.getRunDAO(), RetrofitBuilder.firebaseDatabase) }

    val sharedPreferences by lazy { getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE) }
}