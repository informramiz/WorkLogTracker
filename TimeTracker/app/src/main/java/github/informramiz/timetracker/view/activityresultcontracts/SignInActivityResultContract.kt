package github.informramiz.timetracker.view.activityresultcontracts

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import github.informramiz.timetracker.R

class SignInActivityResultContract : ActivityResultContract<Unit, FirebaseUser?>() {
    override fun createIntent(context: Context, input: Unit?): Intent {
        val providers = arrayListOf(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
        )

        return AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setIsSmartLockEnabled(false)
            .setAvailableProviders(providers)
            .setTheme(R.style.Theme_TimeTracker)
            .build()
    }

    override fun parseResult(resultCode: Int, intent: Intent?): FirebaseUser? {
        return if (resultCode == Activity.RESULT_OK) {
            FirebaseAuth.getInstance().currentUser
        } else {
            null
        }
    }
}
