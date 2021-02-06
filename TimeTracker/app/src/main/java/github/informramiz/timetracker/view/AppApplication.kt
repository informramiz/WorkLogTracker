package github.informramiz.timetracker.view

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class AppApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.initialize(this)
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        Timber.plant(Timber.DebugTree())
    }
}
