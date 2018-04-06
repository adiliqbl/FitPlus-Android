package app.fitplus.health.system.component;

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import app.fitplus.health.R

class InternetDialog(var context: Context?) {

    private var alertDialog: AlertDialog? = null

    init {
        val inflater: LayoutInflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.dialog_message, null)
        alertDialog = AlertDialog.Builder(context, R.style.AlertDialog)
                .setView(view)
                .create()

        view.findViewById<Button>(R.id.dismiss).setOnClickListener({
            alertDialog!!.dismiss()
        })

        alertDialog!!.setOnDismissListener({
            alertDialog = null
            context = null
            view.findViewById<Button>(R.id.dismiss).setOnClickListener(null)
        })

        alertDialog!!.show()
    }
}
