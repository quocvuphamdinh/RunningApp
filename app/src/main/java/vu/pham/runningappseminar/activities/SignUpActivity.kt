package vu.pham.runningappseminar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import vu.pham.runningappseminar.R

import android.view.ViewGroup

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
import vu.pham.runningappseminar.databinding.ActivitySignUpBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.LoadingDialog
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.SignUpViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.SignUpViewModelFactory


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySignUpBinding
    private lateinit var adapterSpinner:ArrayAdapter<String>
    private lateinit var loadingDialog: LoadingDialog
    private var showPass = false
    private var showPass2 = false
    var sex = ""

    private val viewModel : SignUpViewModel by viewModels{
        SignUpViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_sign_up)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)

        initSpinner()
        loadingDialog = LoadingDialog(this@SignUpActivity)

        binding.textViewGoBackWelcomeScreen.setOnClickListener {
            goBack()
        }
        binding.textViewShowHidePasswordSignUp.setOnClickListener {
            onClickShowPass(1)
        }
        binding.textViewShowHidePasswordSignUp2.setOnClickListener {
            onClickShowPass(2)
        }
        binding.buttonSignUp.setOnClickListener {
            signUpAccount()
        }
    }

    private fun goBack() {
        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    private fun signUpAccount(){
        val username= binding.editTextUsernameSignUp.text.toString().trim()
        val password = binding.editTextPasswordSignUp.text.toString().trim()
        val password2= binding.editTextPasswordSignUp2.text.toString().trim()
        val fullname = binding.editTextFullNameSignup.text.toString().trim()
        val height = binding.editTextHeightSignup.text.toString().trim()
        val weight = binding.editTextWeightSignup.text.toString().trim()
        if(!viewModel.checkInfoUser(username, password, password2, fullname, height, weight)){
            Toast.makeText(this@SignUpActivity, "Please enter your information correctly to create account !", Toast.LENGTH_LONG).show()
        }else{
            if(!viewModel.checkSamePassword(password, password2)){
                Toast.makeText(this@SignUpActivity, "Confirm password must the same as password. Please try again !", Toast.LENGTH_LONG).show()
            }else{
                val user = User(username, password, fullname, sex, height.toInt(), weight.toInt(), 0, "")
                checkEmailAccount(user)
            }
        }
    }

    private fun checkEmailAccount(user: User){
        lifecycleScope.launch {
            loadingDialog.startLoadingDialog()
            val userValid = viewModel.checkEmailAccount(user.getUsername())
            if (userValid.getUsername().isNotEmpty()){
                Toast.makeText(this@SignUpActivity, "This email account has already existed !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }else{
                getUser(user)
            }
        }
    }

    private fun checkUser(user: User){
        viewModel.getUser(user.getUsername(), user.getPassword()).enqueue(object : Callback<User?>{
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val user3 = response.body()
                if(viewModel.checkSameUser(user3?.getUsername()!!, user3.getPassword(), user)){
                    Toast.makeText(this@SignUpActivity, "Sign up success !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                    goToLogin()
                }else{
                    Toast.makeText(this@SignUpActivity, "Sign up failed !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Error: $t !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
        })
    }
    private fun getUser(user: User){
        viewModel.getUser(user.getUsername(), user.getPassword()).enqueue(object : Callback<User?>{
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val user2 = response.body()
                if(viewModel.checkSameUser(user2?.getUsername()!!, user2.getPassword(), user)){
                    Toast.makeText(this@SignUpActivity, "Email and password exist !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                }else{
                    lifecycleScope.launch {
                        viewModel.insertUser(user)
                        checkUser(user)
                    }
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Error: $t !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
        })
    }

    private fun goToLogin() {
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    private fun initSpinner(){
        adapterSpinner = object: ArrayAdapter<String>(this@SignUpActivity, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.spinner_sex_signup)){
            override fun isEnabled(position: Int): Boolean {
                return position != 0
            }
            override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                if(position==0){
                    view.setTextColor(resources.getColor(R.color.grey_100))
                }else{
                    view.setTextColor(resources.getColor(R.color.black))
                }
                return view
            }
        }

        binding.spinnerGioiTinh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItemText = parent.getItemAtPosition(position).toString()
                if (position > 0) {
                    sex = selectedItemText
                }else if(position==0){
                    (view as TextView).setTextColor(resources.getColor(R.color.grey_100))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGioiTinh.adapter = adapterSpinner
    }
    private fun onClickShowPass(option:Int) {
        if(option==1){
            if(showPass){
                showPass=false
                binding.editTextPasswordSignUp.transformationMethod= PasswordTransformationMethod.getInstance()
                binding.textViewShowHidePasswordSignUp.text = "SHOW"
            }else{
                showPass=true
                binding.editTextPasswordSignUp.transformationMethod= HideReturnsTransformationMethod.getInstance()
                binding.textViewShowHidePasswordSignUp.text = "HIDE"
            }
        }else if(option==2){
            if(showPass2){
                showPass2=false
                binding.editTextPasswordSignUp2.transformationMethod= PasswordTransformationMethod.getInstance()
                binding.textViewShowHidePasswordSignUp2.text = "SHOW"
            }else{
                showPass2=true
                binding.editTextPasswordSignUp2.transformationMethod= HideReturnsTransformationMethod.getInstance()
                binding.textViewShowHidePasswordSignUp2.text = "HIDE"
            }
        }

    }
}