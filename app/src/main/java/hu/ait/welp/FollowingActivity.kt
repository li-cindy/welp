package hu.ait.welp

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import hu.ait.welp.adapter.FollowingAdapter
import hu.ait.welp.data.Following
import kotlinx.android.synthetic.main.activity_following.*

class FollowingActivity : AppCompatActivity(), FollowingDialog.FollowingHandler, FirebaseHandler {

    lateinit var followingAdapter : FollowingAdapter
    lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_following)
        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            showAddFollowingDialog()
        }

        followingAdapter = FollowingAdapter(this,
            FirebaseAuth.getInstance().currentUser!!.uid)
        firebaseRepository = FirebaseRepository(this)

        recyclerFollowing.layoutManager = LinearLayoutManager(this)

        recyclerFollowing.adapter = followingAdapter
        firebaseRepository.initFollowing(FirebaseAuth.getInstance().currentUser!!.displayName!!)
    }

    private fun showAddFollowingDialog() {
        FollowingDialog().show(supportFragmentManager, "TAG_FOLLOWING_DIALOG")
    }

    override fun handleDocAdded(dc: DocumentChange) {
        val following = dc.document.toObject(Following::class.java)
        followingAdapter.addFollowing(following, dc.document.id)
    }

    override fun handleDocModified(dc: DocumentChange) {
        Toast.makeText(this@FollowingActivity, "update: ${dc.document.id}", Toast.LENGTH_LONG).show()
    }

    override fun handleDocRemoved(dc: DocumentChange) {
        followingAdapter.removeFollowingByKey(dc.document.id)
    }

    override fun handleError(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
        Toast.makeText(this@FollowingActivity, "listen error: ${e!!.message}", Toast.LENGTH_LONG).show()
    }

    override fun followingCreated(username: Following) {
        val db = FirebaseFirestore.getInstance()
        db.collection("following").add(username).addOnSuccessListener {
            followingAdapter.addFollowing(username, it.id)
        }
    }
}

