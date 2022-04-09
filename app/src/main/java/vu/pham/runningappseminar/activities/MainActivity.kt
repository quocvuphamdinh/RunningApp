package vu.pham.runningappseminar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityMainBinding
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.WelcomeViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.WelcomeViewModelFactory

class MainActivity : AppCompatActivity() {
//    private lateinit var btnCreateAccount:MaterialButton
//    private lateinit var btnSignIn:MaterialButton
    private lateinit var binding:ActivityMainBinding

    private val viewModel :WelcomeViewModel by viewModels{
        WelcomeViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        val isFirstAppOpen = viewModel.getFirstTimeToogle()

        if(!isFirstAppOpen){
            goToHomePage()
        }
        //anhXa()
        goToSignInScreen()
        goToSignUpScreen()
    }


    private fun goToHomePage(){
        val intent = Intent(this@MainActivity, HomeActivity::class.java)
        this.finish()
        startActivity(intent)
    }
    private fun goToSignUpScreen() {
        binding.buttonSignupWelcome.setOnClickListener {
            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
            this.finish()
            startActivity(intent)
        }
    }

    private fun goToSignInScreen() {
        binding.buttonSignInWelcome.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            this.finish()
            startActivity(intent)
        }
    }

//    private fun anhXa() {
//        btnCreateAccount = findViewById(R.id.buttonSignupWelcome)
//        btnSignIn = findViewById(R.id.buttonSignInWelcome)
//    }
}