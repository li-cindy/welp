package hu.ait.welp

import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot

interface FirebaseHandler {
    fun handleDocAdded(dc: DocumentChange)
    fun handleDocModified(dc: DocumentChange)
    fun handleDocRemoved(dc: DocumentChange)
    fun handleError(querySnapshot: QuerySnapshot?, e: FirebaseFirestoreException?)

}