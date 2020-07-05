package fr.dev.majdi.personnacoroutines

import android.app.Application
import com.mapbox.mapboxsdk.Mapbox

/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
class PersonnaApp : Application() {


    override fun onCreate() {
        super.onCreate()
        // Mapbox Access token
        Mapbox.getInstance(applicationContext, getString(R.string.mapbox_access_token))
    }

}