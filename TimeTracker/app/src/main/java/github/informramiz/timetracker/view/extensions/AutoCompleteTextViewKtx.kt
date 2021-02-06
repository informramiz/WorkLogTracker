package github.informramiz.timetracker.view.extensions

import android.widget.AutoCompleteTextView

fun AutoCompleteTextView.setSelectedOption(index: Int) {
    val currentAdapter = adapter
    currentAdapter ?: return
    val itemText = currentAdapter.getItem(index).toString()
    setText(itemText, false)
}
