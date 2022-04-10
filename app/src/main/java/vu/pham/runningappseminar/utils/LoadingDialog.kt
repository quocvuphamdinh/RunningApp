package vu.pham.runningappseminar.utils

import android.app.Activity
import android.app.AlertDialog
import vu.pham.runningappseminar.R

class LoadingDialog(private val activity: Activity) {
    private lateinit var alertDialog:AlertDialog

    fun startLoadingDialog(){
        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        builder.setView(inflater.inflate(R.layout.custom_dialog_loading, null))
        builder.setCancelable(false)
        alertDialog = builder.create()
        alertDialog.show()
    }

    fun dismissDialog(){
        alertDialog.dismiss()
    }
}