package github.informramiz.timetracker.data.extensions

import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend inline fun DatabaseReference.getSingleValue(): DataSnapshot {
    return suspendCancellableCoroutine { continuation ->
        addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    continuation.resumeWith(Result.success(snapshot))
                } catch (e: Exception) {
                    continuation.resumeWithException(e)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                continuation.resumeWithException(error.toException())
            }
        })
    }
}

inline fun <reified T> DatabaseReference.listenToValueUpdates() = callbackFlow<T> {
    addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.getValue<T>()?.let {
                offer(it)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            close(error.toException())
        }
    })

    awaitClose {}
}

inline fun <reified T> DatabaseReference.listenToItemUpdates() = callbackFlow<T> {
    addChildEventListener(object : ChildEventListener {
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
            snapshot.getValue<T>()?.let {
                offer(it)
            }
        }

        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onChildRemoved(snapshot: DataSnapshot) {

        }

        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

        }

        override fun onCancelled(error: DatabaseError) {

        }

    })
    awaitClose {}
}
