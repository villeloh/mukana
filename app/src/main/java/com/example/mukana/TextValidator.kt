package com.example.mukana

import android.text.Editable
import android.widget.TextView
import android.text.TextWatcher

// took this from the internet; its main purpose is to tuck away the two useless overridable methods
abstract class TextValidator(private val textView: TextView) : TextWatcher {

    abstract fun validate(textView: TextView, text: String)

    override fun afterTextChanged(s: Editable) {

        val text = textView.text.toString()
        validate(textView, text)
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) { /* Don't care */
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) { /* Don't care */
    }
} // TextValidator