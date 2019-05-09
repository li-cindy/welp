package hu.ait.welp.data

import com.google.type.LatLng

data class Review(
    var name: String = "",
    var description: String = "",
    var imgUrl: String = "",
    var location: String = "", // TODO: should be LatLng
    var rating: Float = 0.toFloat()
)