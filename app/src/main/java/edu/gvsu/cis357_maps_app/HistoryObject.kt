package edu.gvsu.cis357_maps_app

import android.os.Parcel
import android.os.Parcelable

//object for storing scheduled events
//IDE generated parcelable implementation
data class HistoryObject(
    val labelName: String,
    val location: String,
    val snippet: String,
    val lat: Double,
    val long: Double,
    val minutes: Int,
    val hours: Int,
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(labelName)
        parcel.writeString(location)
        parcel.writeString(snippet)
        parcel.writeDouble(lat)
        parcel.writeDouble(long)
        parcel.writeInt(minutes)
        parcel.writeInt(hours)
        parcel.writeByte(if (monday) 1 else 0)
        parcel.writeByte(if (tuesday) 1 else 0)
        parcel.writeByte(if (wednesday) 1 else 0)
        parcel.writeByte(if (thursday) 1 else 0)
        parcel.writeByte(if (friday) 1 else 0)
        parcel.writeByte(if (saturday) 1 else 0)
        parcel.writeByte(if (sunday) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HistoryObject> {
        override fun createFromParcel(parcel: Parcel): HistoryObject {
            return HistoryObject(parcel)
        }

        override fun newArray(size: Int): Array<HistoryObject?> {
            return arrayOfNulls(size)
        }
    }
}
