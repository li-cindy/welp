package hu.ait.welp

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import hu.ait.welp.adapter.ReviewsAdapter
import hu.ait.welp.data.Review
import kotlinx.android.synthetic.main.activity_display_reviews.*

class DisplayReviewsActivity : AppCompatActivity(), FirebaseHandler {
    lateinit var reviewsAdapter: ReviewsAdapter
    lateinit var firebaseRepository: FirebaseRepository

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.navigation_home -> {

                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_create_review -> {
                var createReviewIntent = Intent()
                createReviewIntent.setClass(this@DisplayReviewsActivity,
                    CreateReviewActivity::class.java)

                startActivity(createReviewIntent)
                return@OnNavigationItemSelectedListener true
            }
            R.id.navigation_map -> {
                var mapsIntent = Intent()
                mapsIntent.setClass(this@DisplayReviewsActivity,
                    MapsActivity::class.java)

                startActivity(mapsIntent)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_reviews)

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        reviewsAdapter = ReviewsAdapter(this,
            FirebaseAuth.getInstance().currentUser!!.uid)
        firebaseRepository = FirebaseRepository(this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerReviews.layoutManager = layoutManager

        recyclerReviews.adapter = reviewsAdapter

        firebaseRepository.initReviews()
    }


    override fun handleDocAdded(dc: DocumentChange) {
        val review = dc.document.toObject(Review::class.java)
        reviewsAdapter.addReview(review, dc.document.id)
    }

    override fun handleDocModified(dc: DocumentChange) {
        Toast.makeText(this@DisplayReviewsActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG).show()
    }

    override fun handleDocRemoved(dc: DocumentChange) {
        reviewsAdapter.removeReviewByKey(dc.document.id)
    }

    override fun handleError(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        Toast.makeText(this@DisplayReviewsActivity, "listen error: ${e!!.message}", Toast.LENGTH_LONG).show()
    }



}
