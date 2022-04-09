package vu.pham.runningappseminar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.database.local.Run
import vu.pham.runningappseminar.databinding.ActivityLoginBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.LoginViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {
    private lateinit var binding:ActivityLoginBinding
    private var showPass = false
    private val viewModel : LoginViewModel by viewModels{
        LoginViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_login)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        binding.textViewBackWelComeScreen2.setOnClickListener {
            goBack()
        }
        binding.textViewForgotPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        binding.textViewShowHidePassword.setOnClickListener {
            onClickShowPass()
        }
        binding.buttonSignIn.setOnClickListener {
            checkUserInServer()
        }
    }

    private fun writePersonalDataToSharedPref(user: User){
        viewModel.writePersonalDataToSharedPref(user)
    }

    private fun checkUserInServer() {
        val username = binding.editTextUsernameLogin.text.toString().trim()
        val password = binding.editTextPasswordLogin.text.toString().trim()
        viewModel.getUser(username, password).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val user = response.body()
                if(viewModel.checkSameUser(username, password, user)){
                    lifecycleScope.launch {
                        viewModel.getAllRunFromRemote(user?.getId()!!).enqueue(object : Callback<List<Run>>{
                            override fun onResponse(call: Call<List<Run>>, response: Response<List<Run>>) {
                                val listRun = response.body()
                                for (run in listRun!!){
                                    viewModel.insertRunLocal(run)
                                }
                                writePersonalDataToSharedPref(user)
                                goToHomePage()
                                Toast.makeText(this@LoginActivity, "Login success !", Toast.LENGTH_LONG).show()
                            }
                            override fun onFailure(call: Call<List<Run>>, t: Throwable) {
                                Toast.makeText(this@LoginActivity, "Error: $t !", Toast.LENGTH_LONG).show()
                            }
                        })
                    }
                }else{
                    Toast.makeText(this@LoginActivity, "Login failed !", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
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
            binding.editTextPasswordLogin.transformationMethod=PasswordTransformationMethod.getInstance()
            binding.textViewShowHidePassword.text = "SHOW"
        }else{
            showPass=true
            binding.editTextPasswordLogin.transformationMethod=HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHidePassword.text = "HIDE"
        }
    }
}