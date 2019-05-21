package hu.ait.welp

import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import com.livinglifetechway.k4kotlin.TAG
import com.livinglifetechway.quickpermissions.annotations.WithPermissions
import hu.ait.welp.data.Review
import kotlin.math.roundToInt

class MapsActivity : AppCompatActivity(),  OnMapReadyCallback, LocationProvider.OnNewLocationAvailable, FirebaseHandler {

    private lateinit var mMap: GoogleMap
    private lateinit var locationProvider: LocationProvider
    private lateinit var firebaseRepository : FirebaseRepository
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        firebaseRepository = FirebaseRepository(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startLocation()
        firebaseRepository.initReviews()
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

    }


    @WithPermissions(
        permissions = [android.Manifest.permission.ACCESS_FINE_LOCATION]
    )
    fun startLocation() {
        locationProvider = LocationProvider(this,
            this)
        locationProvider.startLocationMonitoring()
    }


    override fun onStop() {
        super.onStop()
        locationProvider.stopLocationMonitoring()
    }

    override fun onNewLocation(location: Location) {
        if (lat == 0.0 && lng == 0.0){
            lat = location.latitude
            lng = location.longitude
            var latLng = LatLng(lat, lng)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(14f))
        }
    }


    override fun handleDocAdded(dc: DocumentChange) {
        val review = dc.document.toObject(Review::class.java)
        val markerOptions = MarkerOptions().position(LatLng(review.lat, review.lng)).
            title(review.name).snippet("Rating: ${review.rating} out of 5.0")
        mMap.addMarker(markerOptions)
    }

    override fun handleDocModified(dc: DocumentChange) {
        Toast.makeText(this@MapsActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG).show()
    }

    override fun handleDocRemoved(dc: DocumentChange) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun handleError(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        Toast.makeText(this@MapsActivity, "listen error: ${e!!.message}", Toast.LENGTH_LONG).show()
    }


}
