package vu.pham.runningappseminar.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityForgotPasswordBinding
import vu.pham.runningappseminar.utils.LoadingDialog
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ForgotPasswordViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ForgotPasswordViewModelFactory

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityForgotPasswordBinding
    private lateinit var loadingDialog: LoadingDialog

    private val viewModel : ForgotPasswordViewModel by viewModels{
        ForgotPasswordViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_forgot_password)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)


        loadingDialog = LoadingDialog(this@ForgotPasswordActivity)

        binding.imageViewBackInForgotPassword.setOnClickListener {
            finish()
        }
        binding.buttonResetPassword.setOnClickListener {
            doResetPassword()
        }
    }

    private fun doResetPassword() {
        val userName = binding.editTextResetPassword.text.toString().trim()
        val success = viewModel.checkEmailIsValid(userName)
        if(success){
            lifecycleScope.launch {
                loadingDialog.startLoadingDialog()
                val user = viewModel.checkEmailAccount(userName)
                if(user.getUsername().isNotEmpty()){
                    val message = viewModel.resetPassword(user)
                    loadingDialog.dismissDialog()
                    Toast.makeText(this@ForgotPasswordActivity, message["message"], Toast.LENGTH_LONG).show()
                    if(message["message"]!!.contains("successfully")){
                        finish()
                    }
                }else{
                    delay(1000)
                    loadingDialog.dismissDialog()
                    Toast.makeText(this@ForgotPasswordActivity, "Account invalid", Toast.LENGTH_SHORT).show()
                }
            }
        }else{
            Toast.makeText(this@ForgotPasswordActivity, "Email is not correct format !", Toast.LENGTH_SHORT).show()
        }
    }
}