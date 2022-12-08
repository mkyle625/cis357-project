package edu.gvsu.cis357_maps_app

//DayObject class needed for CheckableSpinner
class DayObject (name: String){
    private var mName: String = name

    fun getName(): String {
        return mName
    }
}