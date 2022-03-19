package vu.pham.runningappseminar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ParentViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ParentViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var txtGoBackWelcomeScreen:TextView
    private lateinit var txtForgotPassword:TextView
    private lateinit var editTextUsername:EditText
    private lateinit var editTextPassword:EditText
    private lateinit var txtShowAndHidePassword:TextView
    private lateinit var btnLogin:MaterialButton
    private var showPass = false
    private val viewModel : ParentViewModel by viewModels{
        ParentViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        anhXa()

        txtGoBackWelcomeScreen.setOnClickListener {
            goBack()
        }
        txtForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtShowAndHidePassword.setOnClickListener {
            onClickShowPass()
        }
        btnLogin.setOnClickListener {
            checkUserInServer()
        }
    }

    private fun writePersonalDataToSharedPref(user: User){
        viewModel.writePersonalDataToSharedPref(user)
    }

    private fun checkUserInServer() {
        val username = editTextUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        viewModel.getUser(username, password).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                val user = response.body()
                if(viewModel.checkSameUser(username, password, user)){
                    Toast.makeText(this@LoginActivity, "Login success !", Toast.LENGTH_LONG).show()
                    writePersonalDataToSharedPref(user!!)
                    goToHomePage()
                }else{
                    Toast.makeText(this@LoginActivity, "Login failed !", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: $t !", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToHomePage(){
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        this.finish()
        startActivity(intent)
    }
    private fun goBack() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        this.finish()
        startActivity(intent)
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

    private fun anhXa() {
        txtGoBackWelcomeScreen = findViewById(R.id.textViewBackWelComeScreen2)
        txtForgotPassword = findViewById(R.id.textViewForgotPassword)
        editTextUsername = findViewById(R.id.editTextUsernameLogin)
        editTextPassword = findViewById(R.id.editTextPasswordLogin)
        txtShowAndHidePassword = findViewById(R.id.textViewShowHidePassword)
        btnLogin = findViewById(R.id.buttonSignIn)
    }
}