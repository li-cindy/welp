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

class MapsActivity : AppCompatActivity(),  OnMapReadyCallback, LocationProvider.OnNewLocationAvailable {

    private lateinit var mMap: GoogleMap
    private lateinit var locationProvider: LocationProvider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startLocation()
        initReviews()
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
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
        var latLng = LatLng(location.latitude, location.longitude)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(14f))
    }



    private fun initReviews() {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("reviews")

        var allReviewsListener = query.addSnapshotListener(
            object: EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        Toast.makeText(this@MapsActivity, "listen error: ${e.message}", Toast.LENGTH_LONG).show()
                        return
                    }

                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                val review = dc.document.toObject(Review::class.java)
                                val markerOptions = MarkerOptions().position(LatLng(review.lat, review.lng)).
                                    title(review.name).snippet("Rating: ${review.rating} out of 5.0")
                                mMap.addMarker(markerOptions)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                Toast.makeText(this@MapsActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG).show()
                            }
                            DocumentChange.Type.REMOVED -> {
                                //TODO: handle this case
                            }
                        }
                    }
                }
            })
    }


}
