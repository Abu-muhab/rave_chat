package com.abumuhab.chat.util

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.textfield.TextInputLayout

fun showEditTextError(input: TextInputLayout, message: String) {
    input.isErrorEnabled = true
    input.error = message
}


fun removeEditTextError(input: TextInputLayout) {
    input.isErrorEnabled = false
    input.error = null;
}

fun validateEmailField(value: String): String? {
    if (value.isEmpty()) {
        return "Email Cannot be empty"
    } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(value).matches()) {
        return "Not a valid email address"
    }
    return null
}

fun hideSoftKeyboard(context: Context,view:View){
    val imm: InputMethodManager= context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken,0)
}


fun showBasicMessageDialog(message: String, activity: Activity) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(activity)
    builder.setMessage(message)
    builder.apply {
        this.setPositiveButton("OKAY", DialogInterface.OnClickListener { _, _ ->

        })
    }
    val dialog: AlertDialog? = builder.create()
    dialog?.show()
}