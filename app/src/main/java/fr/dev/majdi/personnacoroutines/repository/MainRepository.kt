package fr.dev.majdi.personnacoroutines.repository

import fr.dev.majdi.personnacoroutines.network.ApiPersonnaHelper

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
class MainRepository(private val apiPersonnaHelper: ApiPersonnaHelper) {
    suspend fun getUsers() = apiPersonnaHelper.getUsers()
}