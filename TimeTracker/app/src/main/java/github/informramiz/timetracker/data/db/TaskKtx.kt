package github.informramiz.timetracker.data.db

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resumeWithException

suspend fun <T> Task<T>.getTaskResult(): T {
    return suspendCancellableCoroutine { continuation ->
        addOnSuccessListener { result ->
            continuation.resumeWith(Result.success(result))
        }

        addOnFailureListener { exception ->
            continuation.resumeWithException(exception)
        }
    }
}
