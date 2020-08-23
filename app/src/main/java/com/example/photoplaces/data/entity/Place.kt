package com.example.photoplaces.data.entity

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
open class Place(
    @PrimaryKey
    var id: String = UUID.randomUUID().toString(),
    var address: String = "",
    var image: String? = null,
    var label: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    @Ignore
    var distance: String? = null
): Parcelable, RealmObject() {

    override fun equals(other: Any?): Boolean {

        if(javaClass != other?.javaClass)
            false

        other as Place

        if(id != other.id)
            false
        if(address != other.address)
            false
        if(image != other.image)
            false
        if(label != other.label)
            false
        if(lat != other.lat)
            false
        if(lng != other.lng)
            false

        return true
    }
}
