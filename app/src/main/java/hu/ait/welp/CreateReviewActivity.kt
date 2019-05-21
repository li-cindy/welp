package hu.ait.welp

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.PointOfInterest
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.livinglifetechway.quickpermissions.annotations.WithPermissions
import hu.ait.welp.data.Review
import kotlinx.android.synthetic.main.activity_create_review.*
import java.io.ByteArrayOutputStream
import java.net.URLEncoder
import java.util.*

class CreateReviewActivity : AppCompatActivity(),
    LocationProvider.OnNewLocationAvailable, OnMapReadyCallback, GoogleMap.OnPoiClickListener{

    private lateinit var locationProvider: LocationProvider
    private var lat: Double = 0.0
    private var lng: Double = 0.0

    companion object {
        private const val PERMISSION_REQUEST_CODE = 101
        private const val CAMERA_REQUEST_CODE = 102
        const val KEY_ITEM_TO_EDIT = "KEY_ITEM_TO_EDIT"
    }

    var uploadBitmap: Bitmap? = null
    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_review)

        btnAttach.setOnClickListener {
            val intentStartCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intentStartCamera, CAMERA_REQUEST_CODE)
        }

        requestNeededPermission()

        mapFragment = supportFragmentManager.findFragmentById(R.id.createReviewMap) as SupportMapFragment
        mapFragment.getMapAsync(this);
        startLocation()


    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnPoiClickListener(this)
    }


    override fun onPoiClick(poi: PointOfInterest) {
        etName.setText(poi.name)
        lat = poi.latLng.latitude
        lng = poi.latLng.longitude
        tvLocation.text = "Location: ${lat}, ${lng}"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            uploadBitmap = data!!.extras.get("data") as Bitmap
            imgAttach.setImageBitmap(uploadBitmap)
            imgAttach.visibility = View.VISIBLE
        }
    }


    private fun requestNeededPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.CAMERA
                )
            ) {
                Toast.makeText(
                    this,
                    "I need it for camera", Toast.LENGTH_SHORT
                ).show()
            }

            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                PERMISSION_REQUEST_CODE
            )
        } else {
            // we already have permission
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "CAMERA perm granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "CAMERA perm NOT granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun sendClick(v: View) {
        if (etName.text.isNotEmpty() || etDescription.text.isNotEmpty()) {
            if (imgAttach.visibility == View.GONE) {
                uploadPost()
            } else {
                uploadPostWithImage()
            }

        } else {
            if (etName.text.isEmpty()) {
                etName.error = "This field cannot be empty!"
            }
            if (etDescription.text.isEmpty()) {
                etDescription.error = "This field cannot be empty!"
            }
        }

    }

    fun uploadPost(imageUrl: String = "") {
        val review = Review(
            FirebaseAuth.getInstance().currentUser!!.uid,
            FirebaseAuth.getInstance().currentUser!!.displayName!!,
            etName.text.toString(),
            etDescription.text.toString(),
            imageUrl,
            lat,
            lng,
            rbEditRating.rating
        )

        var reviewsCollection = FirebaseFirestore.getInstance().collection(
            "reviews"
        )

        reviewsCollection.add(
            review
        ).addOnSuccessListener {
            Toast.makeText(
                this@CreateReviewActivity,
                "Post saved", Toast.LENGTH_LONG
            ).show()

            finish()
        }.addOnFailureListener {
            Toast.makeText(
                this@CreateReviewActivity,
                "Error: ${it.message}", Toast.LENGTH_LONG
            ).show()
        }

        onStop()
    }


    @Throws(Exception::class)
    private fun uploadPostWithImage() {

        val baos = ByteArrayOutputStream()
        uploadBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageInBytes = baos.toByteArray()

        val storageRef = FirebaseStorage.getInstance().getReference()
        val newImage = URLEncoder.encode(UUID.randomUUID().toString(), "UTF-8") + ".jpg"
        val newImagesRef = storageRef.child("images/$newImage")

        newImagesRef.putBytes(imageInBytes)
            .addOnFailureListener { exception ->
                Toast.makeText(this@CreateReviewActivity, exception.message, Toast.LENGTH_SHORT).show()
            }.addOnSuccessListener { taskSnapshot ->
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.

                newImagesRef.downloadUrl.addOnCompleteListener(object : OnCompleteListener<Uri> {
                    override fun onComplete(task: Task<Uri>) {
                        uploadPost(task.result.toString())
                    }
                })
            }
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
            var latLng = com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude)
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
            mMap.animateCamera(CameraUpdateFactory.zoomTo(19f))
            lat = location.latitude
            lng = location.longitude
            tvLocation.text = "Location: ${lat}, ${lng}"
        }
    }

    
}
