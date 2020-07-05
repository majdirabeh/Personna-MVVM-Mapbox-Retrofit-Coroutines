package fr.dev.majdi.personna.model

import java.io.Serializable

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
data class Name(
    val first: String,
    val last: String,
    val title: String
) : Serializable {
    override fun toString(): String {
        return "$first $last"
    }
}