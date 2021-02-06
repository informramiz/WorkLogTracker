package github.informramiz.timetracker.view.extensions

import android.widget.EditText

var EditText.stringText: String
    get() = text?.toString() ?: ""
    set(value) {
        setText(value)
        setSelection(value.length)
    }

