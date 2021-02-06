package github.informramiz.timetracker.data.db

import com.google.android.gms.tasks.Task
import com.google.firebase.functions.FirebaseFunctions

fun <T> FirebaseFunctions.getHttpsCallableSuspendable(functionName: String, data: Any? = null): Task<T> {
    return getHttpsCallable(functionName)
        .call(data)
        .continueWith {
            @Suppress("UNCHECKED_CAST")
            val result = it.result?.data as T
            result
        }
}
