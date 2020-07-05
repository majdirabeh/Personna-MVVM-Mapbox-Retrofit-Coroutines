package fr.dev.majdi.personnacoroutines.network

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
class ApiPersonnaHelper(
    private val apiPersonna: ApiPersonna,
    private val numberPersonna: Int,
    private val nationality: String
) {
    suspend fun getUsers() = apiPersonna.getAllUsers(numberPersonna, nationality)
}