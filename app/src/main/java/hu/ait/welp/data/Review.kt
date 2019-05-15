package hu.ait.welp.data

data class Review(
    var uid: String = "",
    var author: String = "",
    var name: String = "",
    var description: String = "",
    var imgUrl: String = "",
    var lat: Double = 0.0,
    var lng: Double = 0.0,
    var rating: Float = 0.toFloat()
)