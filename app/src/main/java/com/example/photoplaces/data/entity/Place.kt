package com.example.photoplaces.data.entity

import android.os.Parcelable
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
open class Place(
    @PrimaryKey
    var id: Int = 0,
    var address: String = "",
    var image: String? = null,
    var label: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    @Ignore
    var distance: String? = null
): Parcelable, RealmObject()
