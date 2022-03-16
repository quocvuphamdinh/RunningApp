package vu.pham.runningappseminar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.google.android.material.button.MaterialButton
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.utils.Constants.ACTION_SHOW_TRACKING_ACTIVITY

class MainActivity : AppCompatActivity() {
    private lateinit var btnCreateAccount:MaterialButton
    private lateinit var btnSignIn:MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        anhXa()
        goToSignInScreen()
        goToSignUpScreen()
    }


    private fun goToSignUpScreen() {
        btnCreateAccount.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            this.finish()
            startActivity(intent)
        }
    }

    private fun goToSignInScreen() {
        btnSignIn.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            this.finish()
            startActivity(intent)
        }
    }

    private fun anhXa() {
        btnCreateAccount = findViewById(R.id.buttonSignupWelcome)
        btnSignIn = findViewById(R.id.buttonSignInWelcome)
    }
}