package pro.lj.woodie02.repositories

import pro.lj.woodie02.api.RetrofitInstance

class MainRepository {

    suspend fun fetchWeather(lat: String , lon: String)=
        RetrofitInstance.api.getCurrentWeather(lat,lon)
}