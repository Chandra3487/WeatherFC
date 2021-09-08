package com.anirudroidz.currentweatherforcast.ui.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.anirudroidz.currentweatherforcast.OpenWeatherWorker
import com.anirudroidz.currentweatherforcast.ui.main.data.OpenWeatherRepository
import com.anirudroidz.currentweatherforcast.ui.main.data.model.CurrentWeather
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MainViewModel(val currentWeatherLiveData: MutableLiveData<CurrentWeather>) : ViewModel() {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openweathermap.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var cityName: String? = null

    private fun scheduleWork() {
        fetchData()
        val workRequest = PeriodicWorkRequestBuilder<OpenWeatherWorker>(2, TimeUnit.HOURS)
            .build()
        val workManager = WorkManager.getInstance()
        workManager.enqueue(workRequest)
    }

    fun fetchData() {
        val openWeatherRepository = retrofit.create(OpenWeatherRepository::class.java)
        cityName?.let {
            openWeatherRepository.currentWeather(it, "e3f00d5bc092bd1666e378f9ddb0aa37")
                .enqueue(object : Callback<CurrentWeather> {
                    override fun onResponse(call: Call<CurrentWeather>, response: Response<CurrentWeather>) {
                        Log.e(TAG, response.body().toString())
                        currentWeatherLiveData.value = response.body()
                    }

                    override fun onFailure(call: Call<CurrentWeather>, t: Throwable) {
                        Log.e(TAG, t.message ?: "")
                    }
                })
        }
    }

    fun setCityName(cityName: String?) {
        this.cityName = cityName

        scheduleWork()
    }

    companion object {
        private const val TAG = "MainViewModel"
    }
}
