package fr.dev.majdi.personnacoroutines.viewmodel

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
data class Resource<out T>(val status: Status, val data: T?, val message: String?) {
    companion object {
        suspend fun <T> success(data: T): Resource<T> =
            Resource(status = Status.SUCCESS, data = data, message = null)

        suspend fun <T> error(data: T?, message: String): Resource<T> =
            Resource(status = Status.ERROR, data = data, message = message)

        suspend fun <T> loading(data: T?): Resource<T> =
            Resource(status = Status.LOADING, data = data, message = null)
    }
}