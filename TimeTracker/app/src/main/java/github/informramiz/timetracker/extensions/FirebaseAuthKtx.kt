package github.informramiz.timetracker.extensions

import com.google.firebase.auth.FirebaseAuth

val FirebaseAuth.isUserLoggedIn: Boolean
    get() = currentUser != null
