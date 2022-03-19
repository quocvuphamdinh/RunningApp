package vu.pham.runningappseminar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.model.Sex

import android.view.ViewGroup

import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.*
import androidx.activity.viewModels
import com.google.android.material.button.MaterialButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ParentViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ParentViewModelFactory


class SignUpActivity : AppCompatActivity() {
    private lateinit var txtGoBackWelComeScreen:TextView
    private lateinit var spinnerGioiTinh:Spinner
    private lateinit var listGioiTinh:ArrayList<Sex>
    private lateinit var adapterSpinner:ArrayAdapter<Sex>
    private lateinit var txtPassword:TextView
    private lateinit var txtPassword2:TextView
    private lateinit var edtUsername:TextView
    private lateinit var editTextPassword:EditText
    private lateinit var editTextPassword2:EditText
    private lateinit var editTextFullname:EditText
    private lateinit var editTextHeight:EditText
    private lateinit var editTextWeight:EditText
    private lateinit var btnSignup:MaterialButton
    private var showPass = false
    private var showPass2 = false
    var sex = ""
    private var user2:User? = null

    private val viewModel : ParentViewModel by viewModels{
        ParentViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        anhXa()
        initSpinner()

        txtGoBackWelComeScreen.setOnClickListener {
            goBack()
        }
        txtPassword.setOnClickListener {
            onClickShowPass(1)
        }
        txtPassword2.setOnClickListener {
            onClickShowPass(2)
        }
        btnSignup.setOnClickListener {
            signUpAccount()
        }
    }

    private fun goBack() {
        val intent = Intent(this@SignUpActivity, MainActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    private fun signUpAccount(){
        val username= edtUsername.text.toString().trim()
        val password = editTextPassword.text.toString().trim()
        val password2= editTextPassword2.text.toString().trim()
        val fullname = editTextFullname.text.toString().trim()
        val height = editTextHeight.text.toString().trim()
        val weight = editTextWeight.text.toString().trim()
        if(!viewModel.checkInfoUser(username, password, password2, fullname, height, weight)){
            Toast.makeText(this@SignUpActivity, "Please enter your information to create account !", Toast.LENGTH_LONG).show()
        }else{
            if(!viewModel.checkSamePassword(password, password2)){
                Toast.makeText(this@SignUpActivity, "Confirm password must the same as password. Please try again !", Toast.LENGTH_LONG).show()
            }else{
                val user = User(username, password, fullname, sex, weight.toInt(), height.toInt())
                getUser(user)
            }
        }
    }
    private fun checkUser(user: User){
        viewModel.getUser(user.getUsername(), user.getPassword()).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                user2 = response.body()
                if(viewModel.checkSameUser(user2?.getUsername()!!, user2?.getPassword()!!, user)){
                    Toast.makeText(this@SignUpActivity, "Sign up success !", Toast.LENGTH_LONG).show()
                    goToLogin()
                }else{
                    Toast.makeText(this@SignUpActivity, "Sign up failed !", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Error: $t !", Toast.LENGTH_LONG).show()
            }

        })
    }
    private fun getUser(user: User){
        viewModel.getUser(user.getUsername(), user.getPassword()).enqueue(object : Callback<User>{
            override fun onResponse(call: Call<User>, response: Response<User>) {
                user2 = response.body()
                if(viewModel.checkSameUser(user2?.getUsername()!!, user2?.getPassword()!!, user)){
                    Toast.makeText(this@SignUpActivity, "Account exist !", Toast.LENGTH_LONG).show()
                }else{
                    //Toast.makeText(this@SignUpActivity, "Account valid !", Toast.LENGTH_LONG).show()
                    viewModel.insertUser(user)
                    checkUser(user)
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "Error: $t !", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun goToLogin() {
        val intent = Intent(this@SignUpActivity, LoginActivity::class.java)
        this.finish()
        startActivity(intent)
    }

    private fun initSpinner(){
        listGioiTinh = ArrayList()
        listGioiTinh.add(Sex(-1, "Gender"))
        listGioiTinh.add(Sex(1, "Nam"))
        listGioiTinh.add(Sex(2, "Nữ"))
        listGioiTinh.add(Sex(3, "Khác"))
        adapterSpinner = object: ArrayAdapter<Sex>(this@SignUpActivity, android.R.layout.simple_list_item_1, listGioiTinh){
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

        spinnerGioiTinh.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                val selectedItemText = parent.getItemAtPosition(position).toString()
                if (position > 0) {
                    sex = selectedItemText
                    //Toast.makeText(this@SignUpActivity, "Selected : $sex", Toast.LENGTH_SHORT).show()
                }else if(position==0){
                    (view as TextView).setTextColor(resources.getColor(R.color.grey_100))
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerGioiTinh.adapter = adapterSpinner
    }
    private fun onClickShowPass(option:Int) {
        if(option==1){
            if(showPass){
                showPass=false
                editTextPassword.transformationMethod= PasswordTransformationMethod.getInstance()
                txtPassword.text = "SHOW"
            }else{
                showPass=true
                editTextPassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
                txtPassword.text = "HIDE"
            }
        }else if(option==2){
            if(showPass2){
                showPass2=false
                editTextPassword2.transformationMethod= PasswordTransformationMethod.getInstance()
                txtPassword2.text = "SHOW"
            }else{
                showPass2=true
                editTextPassword2.transformationMethod= HideReturnsTransformationMethod.getInstance()
                txtPassword2.text = "HIDE"
            }
        }

    }

    private fun anhXa() {
        spinnerGioiTinh = findViewById(R.id.spinnerGioiTinh)
        txtGoBackWelComeScreen = findViewById(R.id.textViewGoBackWelcomeScreen)
        txtPassword = findViewById(R.id.textViewShowHidePasswordSignUp)
        txtPassword2 = findViewById(R.id.textViewShowHidePasswordSignUp2)
        editTextPassword = findViewById(R.id.editTextPasswordSignUp)
        editTextPassword2 = findViewById(R.id.editTextPasswordSignUp2)
        btnSignup = findViewById(R.id.buttonSignUp)
        edtUsername = findViewById(R.id.editTextUsernameSignUp)
        editTextFullname = findViewById(R.id.editTextFullNameSignup)
        editTextWeight = findViewById(R.id.editTextWeightSignup)
        editTextHeight = findViewById(R.id.editTextHeightSignup)
    }
}