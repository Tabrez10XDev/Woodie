package pro.lj.woodie02.repositories

import pro.lj.woodie02.api.RetrofitInstance
import retrofit2.Response

class MainRepository {

    suspend fun fetchWeather(lat: String , lon: String)=
        RetrofitInstance.api.getCurrentWeather(lat,lon)
}