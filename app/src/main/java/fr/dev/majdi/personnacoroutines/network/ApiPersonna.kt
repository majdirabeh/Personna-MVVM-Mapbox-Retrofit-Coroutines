package fr.dev.majdi.personnacoroutines.network

import fr.dev.majdi.personna.model.ResponsePersonna
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
interface ApiPersonna {
    @GET("/api/")
    suspend fun getAllUsers(
        @Query("results") results: Int,
        @Query("nat") nat: String
    ): ResponsePersonna
}