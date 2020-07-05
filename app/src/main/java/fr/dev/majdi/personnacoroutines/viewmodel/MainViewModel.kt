package fr.dev.majdi.personnacoroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import fr.dev.majdi.personnacoroutines.repository.MainRepository
import kotlinx.coroutines.Dispatchers
import retrofit2.await

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
class MainViewModel(private val mainRepository: MainRepository) : ViewModel() {
    fun getUsers() = liveData(Dispatchers.IO) {
        emit(Resource.loading(data = null))
        try {
            emit(Resource.success(mainRepository.getUsers()))
        } catch (exception: Exception) {
            emit(Resource.error(data = null, message = exception.message ?: "Error Occurred!"))
        }
    }
}