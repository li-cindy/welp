package hu.ait.welp.data

import com.google.type.LatLng

data class Review(
    var uid: String = "",
    var author: String = "",
    var name: String = "",
    var description: String = "",
    var imgUrl: String = "",
    var location: String = "", // TODO: should be LatLng
    var rating: Int = 0
)