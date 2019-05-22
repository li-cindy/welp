package hu.ait.welp

import android.content.Context
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.*
import hu.ait.welp.data.Review

class FirebaseRepository(private val context: FirebaseHandler){

    fun initReviews(author: String) {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("reviews").whereEqualTo("author", author)

        var allReviewsListener = query.addSnapshotListener(
            object: EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        context.handleError(querySnapshot, e)
                        return
                    }

                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                context.handleDocAdded(dc)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                context.handleDocModified(dc)
                            }
                            DocumentChange.Type.REMOVED -> {
                                context.handleDocRemoved(dc)
                            }
                        }
                    }
                }
            })
    }

    fun initFollowing(author: String) {
        val db = FirebaseFirestore.getInstance()

        val query = db.collection("following").whereEqualTo("author", author)

        var allFollowingListener = query.addSnapshotListener(
            object: EventListener<QuerySnapshot> {
                override fun onEvent(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?) {
                    if (e != null) {
                        context.handleError(querySnapshot, e)
                        return
                    }

                    for (dc in querySnapshot!!.getDocumentChanges()) {
                        when (dc.getType()) {
                            DocumentChange.Type.ADDED -> {
                                context.handleDocAdded(dc)
                            }
                            DocumentChange.Type.MODIFIED -> {
                                context.handleDocModified(dc)
                            }
                            DocumentChange.Type.REMOVED -> {
                                context.handleDocRemoved(dc)
                            }
                        }
                    }
                }
            })

    }
}