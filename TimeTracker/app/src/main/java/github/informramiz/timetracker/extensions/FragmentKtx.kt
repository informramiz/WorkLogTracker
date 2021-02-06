package github.informramiz.timetracker.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

fun Fragment.showToast(msg: String) {
    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToast(@StringRes msg: Int) {
    showToast(getString(msg))
}

fun Fragment.requireAppCompatActivity() = (activity as AppCompatActivity)

val Fragment.supportActionBar: ActionBar?
    get() = requireAppCompatActivity().supportActionBar

