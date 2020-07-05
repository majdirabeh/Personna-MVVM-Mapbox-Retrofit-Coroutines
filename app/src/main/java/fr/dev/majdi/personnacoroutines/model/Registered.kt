package fr.dev.majdi.personna.model

import java.io.Serializable

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
data class Registered(
    val age: Int,
    val date: String
) : Serializable