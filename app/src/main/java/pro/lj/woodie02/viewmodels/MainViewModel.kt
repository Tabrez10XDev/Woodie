package pro.lj.woodie02.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import pro.lj.woodie02.data.Weather
import pro.lj.woodie02.repositories.MainRepository
import pro.lj.woodie02.utils.Resource

class MainViewModel(
    private val mainRepository: MainRepository
): ViewModel() {

    val liveWeather : MutableLiveData<Resource<Weather>> = MutableLiveData()





    fun getWeather(lat : String, lon: String) = viewModelScope.launch {
        val response = mainRepository.fetchWeather(lat,lon)
        if(response.isSuccessful){
            liveWeather.postValue(Resource.success(response.body()))
        }
        else{
            liveWeather.postValue(Resource.error(response.message(), null))
        }
    }

}