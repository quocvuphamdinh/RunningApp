package vu.pham.runningappseminar.database

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vu.pham.runningappseminar.utils.Constants

object RetrofitBuilder {
    val gson: Gson = GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create()
    val firebaseDatabase : FirebaseRun = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(FirebaseRun::class.java)
}