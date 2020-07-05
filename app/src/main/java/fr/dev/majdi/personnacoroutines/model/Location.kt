package fr.dev.majdi.personna.model

import java.io.Serializable

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
data class Location(
    val city: String,
    val coordinates: Coordinates,
    val country: String,
    val postcode: Any,
    val state: String,
    val street: Street,
    val timezone: Timezone
) : Serializable