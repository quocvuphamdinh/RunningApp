package vu.pham.runningappseminar.ui.utils

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Window
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.CustomDialogLoadingBinding

class LoadingDialog: DialogFragment() {
    private lateinit var binding: CustomDialogLoadingBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        binding = DataBindingUtil.inflate(LayoutInflater.from(requireContext()), R.layout.custom_dialog_loading, null, false)
        return Dialog(requireContext()).apply {
            setCanceledOnTouchOutside(false)
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
        }
    }

    fun dismissDialog(){
        dialog?.dismiss()
    }
}