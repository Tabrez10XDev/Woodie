package pro.lj.woodie02.api

import pro.lj.woodie02.data.Weather
import pro.lj.woodie02.utils.Const.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherAPI {
        @GET("data/2.5/weather")
        suspend fun getCurrentWeather(
            @Query("lat")
            lat: String = "0",
            @Query("lon")
            lon: String = "0",
            @Query("apiKey")
            appid : String = API_KEY
        ): Response<Weather>


}