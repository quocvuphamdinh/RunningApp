package vu.pham.runningappseminar.utils

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import vu.pham.runningappseminar.database.RunningDatabase
import vu.pham.runningappseminar.repositories.MainRepository

class RunApplication : Application() {
    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { RunningDatabase.getInstance(this) }
    val repository by lazy { MainRepository(database.getRunDAO()) }
}