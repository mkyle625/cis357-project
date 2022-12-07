package edu.gvsu.cis357_maps_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
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


    override fun onCreate(savedInstanceState: Bundle?) {
        print("test")
        Log.i("test", "onCreate: ")
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

        findViewById<Button>(R.id.saveButton).setOnClickListener { view ->
            print("test")
            Log.i("tester", "onCreate: ")
            val textBox = findViewById<EditText>(R.id.editTextClassLabel)
            if(textBox.text.isNotEmpty()){
                textName = textBox.text.toString()
            }
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
                val marker = getMarkerFromString(location!!)
                val minutes = time!!.minutes
                val hours = time!!.hours

                history.add(HistoryObject(textName!!,
                    marker.name!!,
                    marker.snippet!!, marker.lat, marker.long, minutes, hours, mondaySelected, tuesdaySelected, wednesdaySelected, thursdaySelected, fridaySelected, saturdaySelected, sundaySelected))
            }
            else{
                Log.i("textNameBool", labelGiven.toString())
                Log.i("timeBool", timeGiven.toString())
                Log.i("locationBool", locationGiven.toString())
                Log.i("daySelectedBool", daySelected.toString())

                //TODO give warning if not all populated
            }
        }
    }

    private fun getMarkerFromString(input: String): Marker{
        Log.i("string ", input)
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
        Log.i("hours", time!!.hours.toString())
        Log.i("minutes", time!!.minutes.toString())


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

    //TODO add function to get building value from picker

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.schedule_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        //TODO send history back
        if (item.itemId == R.id.action_back) {
            val intent = Intent()
            val extras = Bundle()
            extras.putParcelableArrayList("HISTORY", history)
            intent.putExtras(extras)
            setResult(RESULT_OK, intent)
            finish()
            return true
        }
        return false
    }

}