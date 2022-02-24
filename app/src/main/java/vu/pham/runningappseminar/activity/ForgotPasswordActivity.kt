package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import vu.pham.runningappseminar.R

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var imageViewBack:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        anhXa()

        imageViewBack.setOnClickListener {
            finish()
        }
    }

    private fun anhXa() {
        imageViewBack = findViewById(R.id.imageViewBackInForgotPassword)
    }

}