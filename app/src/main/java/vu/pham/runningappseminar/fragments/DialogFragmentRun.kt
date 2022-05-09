package vu.pham.runningappseminar.fragments

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import vu.pham.runningappseminar.R

class DialogFragmentRun(private val title : String, private val message:String) :  DialogFragment(){

    private var clickYes :(() -> Unit)? = null

    fun setClickYes(click : () -> Unit){
        clickYes = click
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return MaterialAlertDialogBuilder(requireContext(), R.style.AlertDialogTheme)
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.ic_warning)
            .setPositiveButton("Yes", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    clickYes?.let { yes ->
                        yes()
                    }
                }
            })
            .setNegativeButton("No", object : DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, which: Int) {
                    dialog?.cancel()
                }
            }).create()
    }
}