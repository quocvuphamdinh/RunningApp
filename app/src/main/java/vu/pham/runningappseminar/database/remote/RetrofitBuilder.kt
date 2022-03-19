package vu.pham.runningappseminar.database.remote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import vu.pham.runningappseminar.utils.Constants

object RetrofitBuilder {
    private val gson: Gson = GsonBuilder().setDateFormat("dd-MM-yyyy HH:mm:ss").create()
    val FIREBASE_DATABASE : ApiService = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL2)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
}