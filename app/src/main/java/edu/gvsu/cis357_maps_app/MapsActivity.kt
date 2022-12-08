package edu.gvsu.cis357_maps_app

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.icu.util.Calendar
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.contract.ActivityResultContracts

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import edu.gvsu.cis357_maps_app.databinding.ActivityMapsBinding

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private var markers: ArrayList<Marker> = ArrayList()
    var history: ArrayList<HistoryObject> = ArrayList()
    private var onMapMarkers: ArrayList<com.google.android.gms.maps.model.Marker> = ArrayList()


    var selectedLocation: String = "none"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Add the markers
        addMarkers()
    }

    private fun addMarkers() {
        // All the buildings go here
        markers.add(Marker("Mackinac Hall", "MAK", 42.96659164804782, -85.88666565494535))
        markers.add(Marker("Manitou Hall", "MAN", 42.9662006256129, -85.88727636519421))
        markers.add(Marker("Padnos Hall of Science", "PAD", 42.96520949618112, -85.88743079698898))
        markers.add(Marker("Loutit Lecture Halls", "LOU", 42.96506869892937, -85.88820042430099))
        markers.add(Marker("Henry Hall", "HRY", 42.964727820032046, -85.88826624767209))
        markers.add(Marker("Kindschi Hall of Science", "KHS", 42.96583417192454, -85.88916328673147))
        markers.add(Marker("Fieldhouse", "---", 42.96766498636191, -85.88964452838229))
        markers.add(Marker("Rec Center", "---", 42.96635871063172, -85.8901783137834))
        markers.add(Marker("Zumberge Hall", "JHZ", 42.96294819888922, -85.886871878526))
        markers.add(Marker("Lake Superior Hall", "LSH", 42.96203720957562, -85.88663759880195))
        markers.add(Marker("Lake Michigan Hall", "LMH", 42.96139810397481, -85.88616153940927))
        markers.add(Marker("Lake Ontario Hall", "LOH", 42.961359661316735, -85.88510107608539))
        markers.add(Marker("Lake Huron Hall", "LHH", 42.96267630853912, -85.88521270381064))
        markers.add(Marker("Au Sable Hall", "ASH", 42.963234185642094, -85.88544012676792))
        markers.add(Marker("Calder Fine Arts Center", "CAC", 42.961243306036714, -85.88306139124832))
        markers.add(Marker("Niemeyer Honors College", "HON", 42.959924364341504, -85.88573370728348))
        markers.add(Marker("The Blue Connection", "CON", 42.95978323769912, -85.88845960262452))
        markers.add(Marker("Mary Idema Pew Library", "LIB", 42.96295970019208, -85.88987411505184))


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
        val success = googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(this, R.raw.map_style))

        // Add the markers and move the camera
        val allendaleLocation = LatLng(42.96349236278699, -85.89065017075002)

        //add all the markers to the map and add them to list
        for (marker in markers) {
            mMap.addMarker(MarkerOptions().position(LatLng(marker.lat, marker.long)).title(marker.name).snippet(marker.snippet))
                ?.let { onMapMarkers.add(it) }
        }

        //set map centered on campus and set zoom level
        mMap.moveCamera(CameraUpdateFactory.newLatLng(allendaleLocation))
        mMap.setMinZoomPreference(15.0f)
        mMap.setMaxZoomPreference(20.0f)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_schedule) {
            // send history and markers to scheduleActivity and launch scheduleActivity
            val toSchedule = Intent(this@MapsActivity, ScheduleActivity::class.java)
            val extras = Bundle()
            extras.putParcelableArrayList("HISTORY", history)
            extras.putParcelableArrayList("MARKERS", markers)
            extras.putString("LOCATION_TAPPED", "none")
            toSchedule.putExtras(extras)
            scheduleLauncher.launch(toSchedule)
            return true
        }
        return false
    }

    var scheduleLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val extras = data?.extras
            if (extras != null) {
                history = extras.getParcelableArrayList<HistoryObject>("HISTORY") as ArrayList<HistoryObject>
                val locationTapped: String? = extras.getString("LOCATION_TAPPED")

                //check if optional "LOCATION_TAPPED" has value
                if(locationTapped != null && locationTapped.isNotEmpty()){

                    //reset all of the marker colors to red
                    for(marker in onMapMarkers){
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(
                            BitmapDescriptorFactory.HUE_RED))
                    }

                    // check that locationTapped is not "none" then set focus on point
                    if(locationTapped != "none"){
                        selectedLocation = locationTapped

                        var index = 0
                        // If the selected location is not null, center the map on it
                        for (marker in markers) {
                            if (marker.name == selectedLocation) {
                                //center map around point
                                val tempLatlng = LatLng(marker.lat, marker.long)
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(tempLatlng))
                                mMap.setMinZoomPreference(17.0f)
                                mMap.setMaxZoomPreference(20.0f)

                                //set marker color to green
                                onMapMarkers[index].setIcon(BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN))

                                //add blue circle using maps SDK
                                mMap.addCircle(CircleOptions().center(tempLatlng).radius(15.0).fillColor(
                                    Color.argb(50, 25, 25, 150)))
                            }
                            index += 1
                        }
                    }
                    else{
                        //center map on marker for class closest in time in future in same day
                        val c = Calendar.getInstance()
                        val hour = c.get(Calendar.HOUR_OF_DAY)
                        val minute = c.get(Calendar.MINUTE)
                        val day = c.get(Calendar.DAY_OF_WEEK)
                        var closestHours = 999
                        var closestMinutes = 999
                        var closestMarker = ""
                        if(history.size > 0){
                            //determine closest marker by time within the current day
                            if(day == 1){
                                for(obj in history){
                                    if(obj.sunday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }
                            if(day == 2){
                                for(obj in history){
                                    if(obj.monday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }
                            if(day == 3){
                                for(obj in history){
                                    if(obj.tuesday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }
                            if(day == 4){
                                for(obj in history){
                                    if(obj.wednesday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }
                            if(day == 5){
                                for(obj in history){
                                    if(obj.thursday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }
                            if(day == 6){
                                for(obj in history){
                                    if(obj.friday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }
                            if(day == 7){
                                for(obj in history){
                                    if(obj.saturday){
                                        if (obj.hours >= hour && obj.hours <= closestHours && obj.minutes >= minute && obj.minutes <= closestMinutes){
                                            closestHours = obj.hours
                                            closestMinutes = obj.minutes
                                            closestMarker = obj.location
                                        }
                                    }
                                }
                            }

                            //update camera and zoom to center around marker, add yellow circle
                            var index = 0
                            for (marker in markers) {
                                if (marker.name == closestMarker) {
                                    //center map around point
                                    val temp_latlng = LatLng(marker.lat, marker.long)
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(temp_latlng))
                                    mMap.setMinZoomPreference(17.0f)
                                    mMap.setMaxZoomPreference(20.0f)

                                    //set marker color to cyan
                                    onMapMarkers[index].setIcon(BitmapDescriptorFactory.defaultMarker(
                                        BitmapDescriptorFactory.HUE_CYAN))

                                    //add yellow circle using maps SDK
                                    mMap.addCircle(CircleOptions().center(temp_latlng).radius(15.0).fillColor(
                                        Color.argb(50, 150, 150, 0)))
                                }
                                index += 1
                            }
                        }
                    }
                }
            }
        }
    }
}