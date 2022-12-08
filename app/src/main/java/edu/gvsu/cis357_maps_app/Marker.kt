package edu.gvsu.cis357_maps_app

import android.os.Parcel
import android.os.Parcelable

//data class for information in Markers
//IDE auto-generated Parcelable implementation
data class Marker(
    val name: String?,
    val snippet: String?,
    val lat: Double,
    val long: Double
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(snippet)
        parcel.writeDouble(lat)
        parcel.writeDouble(long)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Marker> {
        override fun createFromParcel(parcel: Parcel): Marker {
            return Marker(parcel)
        }

        override fun newArray(size: Int): Array<Marker?> {
            return arrayOfNulls(size)
        }
    }
}
