package edu.gvsu.cis357_maps_app

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import edu.gvsu.cis357_maps_app.place.Place
import edu.gvsu.cis357_maps_app.place.PlacesReader
import edu.gvsu.cis357_maps_app.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style));

        // Add the markers and move the camera
        val allendale_location = LatLng(42.96349236278699, -85.89065017075002)

        mMap.addMarker(MarkerOptions().position(LatLng(42.96659164804782, -85.88666565494535)).title("Mackinac Hall").snippet("MAK"))

        //mMap.addMarker(MarkerOptions().position(allendale_location).title("Allendale Campus"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(allendale_location))
        mMap.setMinZoomPreference(15.0f)
        mMap.setMaxZoomPreference(20.0f)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_schedule) {
            // do something
            val toSchedule = Intent(this@MapsActivity, ScheduleActivity::class.java)
            val extras = Bundle()
            //extras.putString("DISTANCE_UNITS", distanceUnits)
            //extras.putString("BEARING_UNITS", bearingUnits)
            toSchedule.putExtras(extras)
            scheduleLauncher.launch(toSchedule)
            //startActivity(toSettings)
            return true
        }
        return false
    }

    var scheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras = data?.extras
            //distanceUnits = extras?.getString("DISTANCE_SELECTION").toString()
            //bearingUnits = extras?.getString("BEARING_SELECTION").toString()
            //calcButton?.performClick()
            //val tv = findViewById<TextView>(R.id.message)
            //vice = data?.getStringExtra("vice") ?: "Steak"
            //tv.text = "Your vice is: " + vice
        }
    }
}