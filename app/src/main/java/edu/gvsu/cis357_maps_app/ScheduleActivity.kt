package edu.gvsu.cis357_maps_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import edu.gvsu.cis357_maps_app.CheckableSpinnerAdapter.SpinnerItem
import java.util.*
import kotlin.collections.ArrayList

class ScheduleActivity : AppCompatActivity() {
    private val spinnerItems: ArrayList<SpinnerItem<DayObject>> = ArrayList()
    private val selectedItems: HashSet<DayObject> = HashSet()
    private var time: TimePickerFragment? = null
    private var textName: String? = null
    private var location: String? = null
    var markers: ArrayList<Marker> = ArrayList()
    var history: ArrayList<HistoryObject> = ArrayList()
    var selectedLocation: String = "none"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedule)
        createDayObjects()

        //get history and markers from bundle
        val extras = intent.extras
        if (extras != null) {
            markers = extras.getParcelableArrayList<Marker>("MARKERS") as ArrayList<Marker>
            history = extras.getParcelableArrayList<HistoryObject>("HISTORY") as ArrayList<HistoryObject>
        }

        //setup checkablespinner to allow a dropdown of checkboxes on spinner click
        val headerText = "Select the days for the event"
        val spinner = findViewById<Spinner>(R.id.CheckableSpinnerItem)
        val adapter: CheckableSpinnerAdapter<DayObject> = CheckableSpinnerAdapter<DayObject>(this, headerText, spinnerItems, selectedItems)
        spinner.adapter = adapter

        //setup spinner for picking location using values/strings.xml array
        val locationSpinner = findViewById<Spinner>(R.id.BuildingPicker)
        val locationAdapter = ArrayAdapter.createFromResource(this, R.array.Building,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )
        locationAdapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        locationSpinner.adapter = locationAdapter
        locationSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                location = adapterView.getItemAtPosition(i) as String
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

        //populate listview with formatted string based on HistoryObject
        val listView = findViewById<ListView>(R.id.scheduleList)
        val textList = kotlin.collections.ArrayList<String>()
        for(obj in history){
            val daysList = kotlin.collections.ArrayList<String>()
            var i = 0
            var daysString = ""
            if(obj.sunday){
                daysList.add("Sunday")
            }
            if(obj.monday){
                daysList.add("Monday")
            }
            if(obj.tuesday){
                daysList.add("Tuesday")
            }
            if(obj.wednesday){
                daysList.add("Wednesday")
            }
            if(obj.thursday){
                daysList.add("Thursday")
            }
            if(obj.friday){
                daysList.add("Friday")
            }
            if(obj.saturday){
                daysList.add("Saturday")
            }

            //create string of days of the week
            for (dayMessage in daysList){
                daysString += dayMessage
                if(i < daysList.size - 1){
                    daysString += "; "
                }
                i += 1
            }

            //create string for time of event from object data
            var timeString = ""
            var hourFormatted = obj.hours % 12
            if(hourFormatted == 0){
                hourFormatted = 12
            }
            timeString += hourFormatted.toString()
            timeString += ":"
            timeString += obj.minutes.toString()
            timeString += if(obj.hours > 12){
                "PM"
            } else{
                "AM"
            }
            textList.add(obj.labelName + ": " + obj.snippet + "; " + timeString + "; " + daysString)
        }

        //setup list view and listener
        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textList)
        listView.adapter = listAdapter

        listView.setOnItemClickListener { _, _, pos, _ ->
            val selectedItem = history[pos]
            selectedLocation = selectedItem.location
        }

        //add to history when save button is pressed
        findViewById<Button>(R.id.saveButton).setOnClickListener { view ->
            val textBox = findViewById<EditText>(R.id.editTextClassLabel)
            if(textBox.text.isNotEmpty()){
                textName = textBox.text.toString()
            }

            //check if all inputs are given
            val timeGiven = time != null
            val labelGiven = textName != null
            val locationGiven = location != null
            val daySelected = selectedItems.size > 0

            if(timeGiven && labelGiven && locationGiven && daySelected){
                var mondaySelected = false
                var tuesdaySelected = false
                var wednesdaySelected = false
                var thursdaySelected = false
                var fridaySelected = false
                var saturdaySelected = false
                var sundaySelected = false

                //convert the string names of the days into boolean values for parcel
                for (day in selectedItems){
                    if (day.getName() == "Monday"){
                        mondaySelected = true
                    }
                    if (day.getName() == "Tuesday"){
                        tuesdaySelected = true
                    }
                    if (day.getName() == "Wednesday"){
                        wednesdaySelected = true
                    }
                    if (day.getName() == "Thursday"){
                        thursdaySelected = true
                    }
                    if (day.getName() == "Friday"){
                        fridaySelected = true
                    }
                    if (day.getName() == "Saturday"){
                        saturdaySelected = true
                    }
                    if (day.getName() == "Sunday"){
                        sundaySelected = true
                    }
                }
                //get the marker represented by the string chosen in the spinner
                val marker = getMarkerFromString(location!!)

                //get the minutes and hours from the timepicker
                val minutes = time!!.minutes
                val hours = time!!.hours

                //create history object from inputs, and add to history list
                history.add(HistoryObject(textName!!,
                    marker.name!!,
                    marker.snippet!!, marker.lat, marker.long, minutes, hours, mondaySelected, tuesdaySelected, wednesdaySelected, thursdaySelected, fridaySelected, saturdaySelected, sundaySelected))

                //populate listview with formatted string based on HistoryObject
                val listView = findViewById<ListView>(R.id.scheduleList)
                val textList = kotlin.collections.ArrayList<String>()
                for(obj in history){
                    val daysList = kotlin.collections.ArrayList<String>()
                    var i = 0
                    var daysString = ""
                    if(obj.sunday){
                        daysList.add("Sunday")
                    }
                    if(obj.monday){
                        daysList.add("Monday")
                    }
                    if(obj.tuesday){
                        daysList.add("Tuesday")
                    }
                    if(obj.wednesday){
                        daysList.add("Wednesday")
                    }
                    if(obj.thursday){
                        daysList.add("Thursday")
                    }
                    if(obj.friday){
                        daysList.add("Friday")
                    }
                    if(obj.saturday){
                        daysList.add("Saturday")
                    }

                    //create string of days of the week
                    for (dayMessage in daysList){
                        daysString += dayMessage
                        if(i < daysList.size - 1){
                            daysString += "; "
                        }
                        i += 1
                    }

                    //create string for time of event from object data
                    var timeString = ""
                    var hourFormatted = obj.hours % 12
                    if(hourFormatted == 0){
                        hourFormatted = 12
                    }
                    timeString += hourFormatted.toString()
                    timeString += ":"
                    timeString += obj.minutes.toString()
                    timeString += if(obj.hours > 12){
                        "PM"
                    } else{
                        "AM"
                    }
                    textList.add(obj.labelName + ": " + obj.snippet + "; " + timeString + "; " + daysString)
                }

                //setup list view and listener
                val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, textList)
                listView.adapter = listAdapter

                listView.setOnItemClickListener { _, _, pos, _ ->
                    val selectedItem = history[pos]
                    selectedLocation = selectedItem.location
                }
            }
        }
    }

    private fun getMarkerFromString(input: String): Marker{
        for( marker in markers){
            if (input.lowercase(Locale.getDefault()) == marker.name!!.lowercase(Locale.getDefault())){
                return marker
            }
        }
        return markers[0]
    }

    fun showTimePickerDialog(view: View) {
        time = TimePickerFragment()
        time!!.show(supportFragmentManager, "timePicker")
    }

    fun createDayObjects(){
        val monObj = DayObject("Monday")
        val tuesObj = DayObject("Tuesday")
        val wedObj = DayObject("Wednesday")
        val thurObj = DayObject("Thursday")
        val friObj = DayObject("Friday")
        val satObj = DayObject("Saturday")
        val sunObj = DayObject("Sunday")

        spinnerItems.add(SpinnerItem<DayObject>(monObj, monObj.getName()))
        spinnerItems.add(SpinnerItem<DayObject>(tuesObj, tuesObj.getName()))
        spinnerItems.add(SpinnerItem<DayObject>(wedObj, wedObj.getName()))
        spinnerItems.add(SpinnerItem<DayObject>(thurObj, thurObj.getName()))
        spinnerItems.add(SpinnerItem<DayObject>(friObj, friObj.getName()))
        spinnerItems.add(SpinnerItem<DayObject>(satObj, satObj.getName()))
        spinnerItems.add(SpinnerItem<DayObject>(sunObj, sunObj.getName()))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_back) {
            //send history and location_tapped to mapactivity
            val intent = Intent()
            val extras = Bundle()
            extras.putParcelableArrayList("HISTORY", history)
            extras.putString("LOCATION_TAPPED", selectedLocation)
            intent.putExtras(extras)
            setResult(RESULT_OK, intent)
            finish()
            return true
        }
        return false
    }

}