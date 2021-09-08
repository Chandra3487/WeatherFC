package com.anirudroidz.currentweatherforcast.ui.main.data

import com.anirudroidz.currentweatherforcast.ui.main.data.model.CurrentWeather
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherRepository {
    @GET("data/2.5/weather")
    fun currentWeather(@Query("q") q: String, @Query("appid") appid: String) : Call<CurrentWeather>
}
