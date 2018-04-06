package app.fitplus.health.system.component;

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.widget.TextView
import app.fitplus.health.R
import android.widget.Toast

class CustomToast(context: Context, activity: Activity, message: String) : Toast(context) {
    init {
        val inflater = activity.layoutInflater
        @SuppressLint("InflateParams") val view = inflater.inflate(R.layout.toast, null)
        val txt = view.findViewById(R.id.message) as TextView
        txt.text = message
        setGravity(Gravity.CENTER_HORIZONTAL or Gravity.BOTTOM, 0, 120)
        duration = Toast.LENGTH_LONG
        setView(view)
    }
}