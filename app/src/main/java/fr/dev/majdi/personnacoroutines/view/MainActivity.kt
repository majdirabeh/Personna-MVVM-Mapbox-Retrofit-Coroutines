package fr.dev.majdi.personnacoroutines.view

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.mapbox.mapboxsdk.annotations.Icon
import com.mapbox.mapboxsdk.annotations.IconFactory
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.geometry.LatLngBounds
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import com.tbruyelle.rxpermissions2.RxPermissions
import fr.dev.majdi.personna.model.Result
import fr.dev.majdi.personnacoroutines.R
import fr.dev.majdi.personnacoroutines.adapter.PersonnaAdapter
import fr.dev.majdi.personnacoroutines.network.ApiPersonnaHelper
import fr.dev.majdi.personnacoroutines.network.RetrofitBuilder
import fr.dev.majdi.personnacoroutines.utils.CircleBubbleTransformation
import fr.dev.majdi.personnacoroutines.utils.Constants.Companion.NATIONALITY
import fr.dev.majdi.personnacoroutines.utils.Constants.Companion.NUMBER_OF_USERS
import fr.dev.majdi.personnacoroutines.viewmodel.MainViewModel
import fr.dev.majdi.personnacoroutines.viewmodel.Status
import fr.dev.majdi.personnacoroutines.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import retrofit2.await


/**
 * Created by Majdi RABEH on 04/07/2020.
 * Email : m.rabeh.majdi@gmail.com
 */
@SuppressLint("CheckResult")
class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: MainViewModel
    private var personnaListAdapter = PersonnaAdapter(listOf())

    //private var mapView: MapView? = null
    private var mapboxMap: MapboxMap? = null
    private var rxPermissions: RxPermissions? = null
    private var currentLocation: LatLng? = null
    private var isMapListButtonClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        setupUI()
        initPermission(savedInstanceState)

    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            ViewModelFactory(
                ApiPersonnaHelper(
                    RetrofitBuilder.apiService,
                    NUMBER_OF_USERS,
                    NATIONALITY
                )
            )
        ).get(MainViewModel::class.java)
    }

    private fun setupUI() {
        recycler.layoutManager = LinearLayoutManager(this)
        personnaListAdapter = PersonnaAdapter(arrayListOf())
        recycler.addItemDecoration(
            DividerItemDecoration(
                recycler.context,
                (recycler.layoutManager as LinearLayoutManager).orientation
            )
        )
        recycler.adapter = personnaListAdapter

        iconMapList.setOnClickListener {
            if (!isMapListButtonClicked) {
                visibilityMap(false)
                iconMapList.setImageResource(R.drawable.ic_baseline_map_24)
                titleToolbar.text = resources.getString(R.string.title_list)
                isMapListButtonClicked = true
            } else {
                visibilityMap(true)
                iconMapList.setImageResource(R.drawable.ic_baseline_format_list_bulleted_24)
                titleToolbar.text = resources.getString(R.string.title_map)
                isMapListButtonClicked = false
            }
        }
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
    }

    private fun visibilityMap(setVisible: Boolean) {
        if (setVisible) {
            mapView!!.visibility = View.VISIBLE
            recycleRelative.visibility = View.GONE
        } else {
            mapView!!.visibility = View.GONE
            recycleRelative.visibility = View.VISIBLE
        }
    }

    private fun setupObservers() {
        viewModel.getUsers().observe(this, Observer {
            it?.let { resource ->
                when (resource.status) {
                    Status.SUCCESS -> {
                        recycler.visibility = View.VISIBLE
                        progressBar.visibility = View.GONE
                        noData.visibility = View.GONE
                        resource.data?.let { data -> refreshList(data.results) }
                    }
                    Status.ERROR -> {
                        runOnUiThread {
                            recycler.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            noData.visibility = View.VISIBLE
                            Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                        }
                    }
                    Status.LOADING -> {
                        runOnUiThread {
                            progressBar.visibility = View.VISIBLE
                            recycler.visibility = View.GONE
                            noData.visibility = View.GONE
                        }
                    }
                }
            }
        })
    }

    private fun refreshList(users: List<Result>) {
        personnaListAdapter.apply {
            setItems(users)
            notifyDataSetChanged()
        }

        GlobalScope.launch {
            withContext(Dispatchers.Main) {
                if (users.isNotEmpty()) {
                    for (i in users.indices) {
                        addCustomMarker(users[i])
                    }
                    if (users.size >= 3) {
                        val latLngBounds = LatLngBounds.Builder()
                            .include(
                                LatLng(
                                    users[0].location.coordinates.latitude.toDouble(),
                                    users[0].location.coordinates.longitude.toDouble()
                                )
                            ) // Northeast
                            .include(
                                LatLng(
                                    users[users.size - 1].location.coordinates.latitude.toDouble(),
                                    users[users.size - 1].location.coordinates.longitude.toDouble()
                                )
                            ) // Southwest
                            .build()
                        mapboxMap!!.easeCamera(
                            CameraUpdateFactory.newLatLngBounds(
                                latLngBounds,
                                50
                            ), 5000
                        )
                    }

                }
            }
        }

    }

    private fun addCustomMarker(user: Result) {
        if (user.picture.medium.isNotEmpty()) {
            Picasso.get()
                .load(user.picture.medium)
                .resize(100, 100)
                .centerCrop()
                .error(R.drawable.user) // On error image
                .placeholder(R.drawable.user) // Place holder image
                .transform(CircleBubbleTransformation())
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {

                        val icon =
                            IconFactory.getInstance(this@MainActivity)
                                .fromBitmap(bitmap)
                        mapboxMap!!.addMarker(
                            MarkerOptions()
                                .position(
                                    LatLng(
                                        user.location.coordinates.latitude.toDouble(),
                                        user.location.coordinates.longitude.toDouble()
                                    )
                                )
                                .icon(icon)
                        )

                    }

                    override fun onBitmapFailed(
                        ex: Exception,
                        errorDrawable: Drawable
                    ) {
                        Log.e("MainActivity %s", ex.toString())
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                })
        } else {
            Picasso.get()
                .load(R.drawable.user)
                .error(R.drawable.user) // On error image
                .placeholder(R.drawable.user) // Place holder image
                .resize(150, 150)
                .centerCrop()
                .transform(CircleBubbleTransformation())
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {

                        val icon =
                            IconFactory.getInstance(this@MainActivity)
                                .fromBitmap(bitmap)
                        mapboxMap!!.addMarker(
                            MarkerOptions()
                                .position(
                                    LatLng(
                                        user.location.coordinates.latitude.toDouble(),
                                        user.location.coordinates.longitude.toDouble()
                                    )
                                )
                                .icon(icon)
                        )

                    }

                    override fun onBitmapFailed(
                        ex: Exception,
                        errorDrawable: Drawable
                    ) {
                        Log.e("MainActivity %s", ex.toString())
                    }

                    override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
                })
        }
    }

    private fun initPermission(savedInstanceState: Bundle?) {
        rxPermissions = RxPermissions(this)
        rxPermissions!!
            .request(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            .subscribe { granted ->
                if (granted) { // Always true pre-M
                    initMapBox(savedInstanceState)
                } else {
                    Toast.makeText(
                        this,
                        "Please accept permission location to start map",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }

    private fun initMapBox(savedInstanceState: Bundle?) {
        //mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { mapbox ->
            mapbox.setStyle(Style.MAPBOX_STREETS) {
                mapboxMap = mapbox
                enableLocationComponent(it)
                setupObservers()
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
        val customLocationComponentOptions = LocationComponentOptions.builder(this)
            .pulseEnabled(true)
            .build()

        val locationComponent = mapboxMap?.locationComponent
        locationComponent?.activateLocationComponent(
            LocationComponentActivationOptions.builder(this, loadedMapStyle)
                .locationComponentOptions(customLocationComponentOptions)
                .build()
        )

        // Enable to make component visible
        locationComponent?.isLocationComponentEnabled = true
        // Set the component's camera mode
        locationComponent?.cameraMode = CameraMode.TRACKING_COMPASS
        // Set the component's render mode
        locationComponent?.renderMode = RenderMode.COMPASS

        currentLocation = LatLng(
            locationComponent?.lastKnownLocation?.latitude!!,
            locationComponent.lastKnownLocation?.longitude!!
        )
//        if (currentLocation != null) {
//            val position = CameraPosition.Builder()
//                .target(
//                    currentLocation
//                ) // Sets the new camera position
//                .zoom(9.0) // Sets the zoom
//                .bearing(180.0) // Rotate the camera
//                .tilt(30.0) // Set the camera tilt
//                .build() // Creates a CameraPosition from the builder
//
//            mapboxMap?.animateCamera(
//                CameraUpdateFactory
//                    .newCameraPosition(position), 7000
//            )
//        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

}