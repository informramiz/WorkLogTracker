package github.informramiz.timetracker.view.validators

import android.util.Patterns

fun String.isValidName(): Boolean {
    return isNotBlank()
}

fun String.isValidEmail(): Boolean {
    return isNotBlank() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

fun String.isValidPassword(): Boolean {
    return isNotBlank() && length >= 6
}
