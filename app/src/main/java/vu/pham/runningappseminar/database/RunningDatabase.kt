package vu.pham.runningappseminar.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room
import vu.pham.runningappseminar.utils.Constants


@Database(
    entities = [Run::class],
    version = 1
)
abstract class RunningDatabase : RoomDatabase() {
    companion object{
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        var instanceVu: RunningDatabase? = null

        fun getInstance(context: Context): RunningDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return instanceVu ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RunningDatabase::class.java,
                    Constants.DATABASE_NAME
                ).build()
                instanceVu = instance
                instance
            }
        }
    }

    abstract fun getRunDAO() : RunDAO
}