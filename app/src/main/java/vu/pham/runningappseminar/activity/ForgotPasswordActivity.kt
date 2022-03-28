package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    //private lateinit var imageViewBack:ImageView
    private lateinit var binding:ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        //anhXa()

        binding.imageViewBackInForgotPassword.setOnClickListener {
            finish()
        }
    }

//    private fun anhXa() {
//        imageViewBack = findViewById(R.id.imageViewBackInForgotPassword)
//    }
}