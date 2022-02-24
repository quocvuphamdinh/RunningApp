package vu.pham.runningappseminar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.android.material.button.MaterialButton
import vu.pham.runningappseminar.R

class LoginActivity : AppCompatActivity() {
    private lateinit var txtGoBackWelcomeScreen:TextView
    private lateinit var txtForgotPassword:TextView
    private lateinit var editTextUsername:EditText
    private lateinit var editTextPassword:EditText
    private lateinit var txtShowAndHidePassword:TextView
    private lateinit var btnLogin:MaterialButton
    private var showPass = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        anhXa()

        txtGoBackWelcomeScreen.setOnClickListener {
            finish()
        }
        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtShowAndHidePassword.setOnClickListener {
            onClickShowPass()
        }
        btnLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
        }
    }

    private fun anhXa() {
        txtGoBackWelcomeScreen = findViewById(R.id.textViewBackWelComeScreen2)
        txtForgotPassword = findViewById(R.id.textViewForgotPassword)
        editTextUsername = findViewById(R.id.editTextUsernameLogin)
        editTextPassword = findViewById(R.id.editTextPasswordLogin)
        txtShowAndHidePassword = findViewById(R.id.textViewShowHidePassword)
        btnLogin = findViewById(R.id.buttonSignIn)
    }

    private fun onClickShowPass() {
        if(showPass){
            showPass=false
            editTextPassword.transformationMethod=PasswordTransformationMethod.getInstance()
            txtShowAndHidePassword.text = "SHOW"
        }else{
            showPass=true
            editTextPassword.transformationMethod=HideReturnsTransformationMethod.getInstance()
            txtShowAndHidePassword.text = "HIDE"
        }
    }
}