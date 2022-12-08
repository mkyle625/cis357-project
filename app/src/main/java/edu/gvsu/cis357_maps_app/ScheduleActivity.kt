package edu.gvsu.cis357_maps_app

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import edu.gvsu.cis357_maps_app.CheckableSpinnerAdapter.SpinnerItem
import java.util.*
import kotlin.collections.ArrayList

class ScheduleActivity : AppCompatActivity() {
    private val spinner_items: ArrayList<SpinnerItem<DayObject>> = ArrayList()
    private val selected_items: HashSet<DayObject> = HashSet()
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
        val extras = intent.extras
        if (extras != null) {
            markers = extras.getParcelableArrayList<Marker>("MARKERS") as ArrayList<Marker>
            history = extras.getParcelableArrayList<HistoryObject>("HISTORY") as ArrayList<HistoryObject>
        }
        val headerText = "Select the days for the event"
        val spinner = findViewById<Spinner>(R.id.CheckableSpinnerItem)
        val adapter: CheckableSpinnerAdapter<DayObject> = CheckableSpinnerAdapter<DayObject>(this, headerText, spinner_items, selected_items)
        spinner.adapter = adapter;

        val location_spinner = findViewById<Spinner>(R.id.BuildingPicker)
        val location_adapter = ArrayAdapter.createFromResource(this, R.array.Building,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )
        location_adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item)
        location_spinner.adapter = location_adapter
        location_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {
                location = adapterView.getItemAtPosition(i) as String
            }
            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }

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

            var timeString = ""
            var hourFormatted = obj.hours % 12
            if(hourFormatted == 0){
                hourFormatted = 12
            }
            timeString += hourFormatted.toString()
            if(obj.hours > 12){
                timeString += "PM"
            } else{
                timeString += "AM"
            }
            textList.add(obj.labelName + ": " + obj.snippet + "; " + timeString + "; " + daysString)
        }
        val listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, history)
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
            val daySelected = selected_items.size > 0

            if(timeGiven && labelGiven && locationGiven && daySelected){
                var mondaySelected: Boolean = false
                var tuesdaySelected: Boolean = false
                var wednesdaySelected: Boolean = false
                var thursdaySelected: Boolean = false
                var fridaySelected: Boolean = false
                var saturdaySelected: Boolean = false
                var sundaySelected: Boolean = false

                //convert the string names of the days into boolean values for parcel
                for (day in selected_items){
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

                val duration = Toast.LENGTH_SHORT
                val text = "Event added to schedule; go to map screen and return to see in history list"
                //show toast to let user know what they need to do
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
            }
            else{
                val errorList = kotlin.collections.ArrayList<String>()
                val duration = Toast.LENGTH_SHORT
                var text = ""
                var index = 0

                //determine which of the inputs was missing and create error message
                if(!timeGiven){
                    errorList.add("no time given for event")
                }
                if(!labelGiven){
                    errorList.add("No name given for event")
                }
                if(!locationGiven){
                    errorList.add("No location given for event")
                }
                if(!daySelected){
                    errorList.add("No days given for event")
                }

                //combine error messages into 1 string for Toast popup
                for (errorMessage in errorList){
                    text += errorMessage
                    if(index < errorList.size - 1){
                        text += "; "
                    }
                    index += 1
                }

                //show toast to let user know what they need to do
                val toast = Toast.makeText(applicationContext, text, duration)
                toast.show()
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
        val monObj: DayObject = DayObject("Monday")
        val tuesObj: DayObject = DayObject("Tuesday")
        val wedObj: DayObject = DayObject("Wednesday")
        val thurObj: DayObject = DayObject("Thursday")
        val friObj: DayObject = DayObject("Friday")
        val satObj: DayObject = DayObject("Saturday")
        val sunObj: DayObject = DayObject("Sunday")

        spinner_items.add(SpinnerItem<DayObject>(monObj, monObj.getName()))
        spinner_items.add(SpinnerItem<DayObject>(tuesObj, tuesObj.getName()))
        spinner_items.add(SpinnerItem<DayObject>(wedObj, wedObj.getName()))
        spinner_items.add(SpinnerItem<DayObject>(thurObj, thurObj.getName()))
        spinner_items.add(SpinnerItem<DayObject>(friObj, friObj.getName()))
        spinner_items.add(SpinnerItem<DayObject>(satObj, satObj.getName()))
        spinner_items.add(SpinnerItem<DayObject>(sunObj, sunObj.getName()))
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_back) {
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