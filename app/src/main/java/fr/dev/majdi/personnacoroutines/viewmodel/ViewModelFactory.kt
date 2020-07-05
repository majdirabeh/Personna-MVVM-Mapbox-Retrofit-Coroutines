package fr.dev.majdi.personnacoroutines.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import fr.dev.majdi.personnacoroutines.network.ApiPersonnaHelper
import fr.dev.majdi.personnacoroutines.repository.MainRepository

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
class ViewModelFactory (private val apiHelper: ApiPersonnaHelper) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(MainRepository(apiHelper)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }

}