package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.EditProfileViewModel
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.EditProfileViewModelFactory
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory

class EditProfileActivity : AppCompatActivity() {
    private lateinit var txtCancel:TextView
    private lateinit var txtSave:TextView
    private lateinit var edtUsername:EditText
    private lateinit var edtPassword:EditText
    private lateinit var edtFullname:EditText
    private lateinit var edtHeight:EditText
    private lateinit var edtWeight:EditText
    private lateinit var spinnerSex:Spinner
    private lateinit var txtShowAndHidePassword:TextView
    private var showPass=false
    var sex = ""
    private var user:User= User()

    private val viewModel : EditProfileViewModel by viewModels{
        EditProfileViewModelFactory((application as RunApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        anhXa()
        setUpSpinnerSex()
        getUserData()

        txtSave.setOnClickListener {
            onClickSave()
        }
        txtShowAndHidePassword.setOnClickListener {
            onClickShowPass()
        }
        txtCancel.setOnClickListener {
            onClickCancel()
        }
    }

    private fun onClickShowPass() {
        if(showPass){
            showPass=false
            edtPassword.transformationMethod= PasswordTransformationMethod.getInstance()
            txtShowAndHidePassword.text = "SHOW"
        }else{
            showPass=true
            edtPassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
            txtShowAndHidePassword.text = "HIDE"
        }
    }
    private fun onClickSave() {
        val username = edtUsername.text.toString().trim()
        val password = edtPassword.text.toString().trim()
        val fullname = edtFullname.text.toString().trim()
        val height = edtHeight.text.toString().trim()
        val weight = edtWeight.text.toString().trim()
        if(viewModel.checkInfoUser(username, password, password, fullname, height, weight)){
            val userNew = User(username, password, fullname, sex, height.toInt(), weight.toInt(), user.getdistanceGoal())
            userNew.setId(user.getId())
            viewModel.updateUser(userNew)
            viewModel.writePersonalDataToSharedPref(userNew)
            this.finish()
        }else{
            Toast.makeText(this@EditProfileActivity, "Please enter your information to update account !", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserData() {
        val bundle = intent?.extras
        user = bundle?.getSerializable(Constants.EDIT_USER) as User
        user.let {
            edtUsername.setText(it.getUsername())
            edtPassword.setText(it.getPassword())
            edtFullname.setText(it.getFullname())
            edtHeight.setText(it.getHeight().toString())
            edtWeight.setText(it.getWeight().toString())
            spinnerSex.setSelection(if(it.getSex()=="Male") 0 else if (it.getSex()=="Female") 1 else 2)
        }
    }

    private fun setUpSpinnerSex() {
        val spinnerAdapter = ArrayAdapter(this@EditProfileActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.spinner_sex))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSex.adapter = spinnerAdapter
        spinnerSex.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sex = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun onClickCancel() {
        finish()
    }

    private fun anhXa() {
        txtCancel = findViewById(R.id.textViewCancelEditProfile)
        txtSave = findViewById(R.id.textViewSaveEditProfile)
        edtUsername = findViewById(R.id.editTextUsernameEditProfile)
        edtPassword = findViewById(R.id.editTextPasswordEditProfile)
        edtFullname = findViewById(R.id.editTextFullnameEditProfile)
        edtHeight = findViewById(R.id.editTextHeightEditProfile)
        edtWeight = findViewById(R.id.editTextWeightEditProfile)
        spinnerSex = findViewById(R.id.spinnerGioiTinhEditProfile)
        txtShowAndHidePassword = findViewById(R.id.textViewShowHidePasswordEditProfile)
    }
}