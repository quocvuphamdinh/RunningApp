package vu.pham.runningappseminar.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityEditProfileBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.EditProfileViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.EditProfileViewModelFactory

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding:ActivityEditProfileBinding
    private var showPass1=false
    private var showPass2=false
    private var showPass3=false
    var sex = ""
    private var user:User= User()

    private val viewModel : EditProfileViewModel by viewModels{
        EditProfileViewModelFactory((application as RunApplication).repository)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_edit_profile)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile)

        //anhXa()
        setUpSpinnerSex()
        getUserData()

        binding.spinnerGioiTinhEditProfile.isEnabled = false
        binding.textViewSaveEditProfile.setOnClickListener {
            onClickSave()
        }
        binding.textViewShowHideOldPasswordEditProfile.setOnClickListener {
            onClickShowPass()
        }
        binding.textViewShowHideNewPasswordEditProfile.setOnClickListener {
            onClickShowPass2()
        }
        binding.textViewShowHideConfirmPasswordEditProfile.setOnClickListener {
            onClickShowPass3()
        }
        binding.textViewCancelEditProfile.setOnClickListener {
            onClickCancel()
        }
        binding.checkBoxPersonalInfo.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.cardViewPersonalInfor.setBackgroundColor(resources.getColor(R.color.white))
                binding.editTextFullnameEditProfile.isEnabled = true
                binding.editTextUsernameEditProfile.isEnabled = true
                binding.editTextHeightEditProfile.isEnabled = true
                binding.editTextWeightEditProfile.isEnabled = true
                binding.spinnerGioiTinhEditProfile.isEnabled = true
            }else{
                binding.cardViewPersonalInfor.setBackgroundColor(resources.getColor(R.color.grey_90))
                binding.editTextFullnameEditProfile.isEnabled = false
                binding.editTextUsernameEditProfile.isEnabled = false
                binding.editTextHeightEditProfile.isEnabled = false
                binding.editTextWeightEditProfile.isEnabled = false
                binding.spinnerGioiTinhEditProfile.isEnabled = false
            }
        }
        binding.checkBoxChangePassword.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked){
                binding.cardViewChangePassword.setBackgroundColor(resources.getColor(R.color.white))
                binding.editTextOldPasswordEditProfile.isEnabled = true
                binding.editTextNewPasswordEditProfile.isEnabled = true
                binding.editTextConfirmPasswordEditProfile.isEnabled = true
            }else{
                binding.cardViewChangePassword.setBackgroundColor(resources.getColor(R.color.grey_90))
                binding.editTextOldPasswordEditProfile.isEnabled = false
                binding.editTextNewPasswordEditProfile.isEnabled = false
                binding.editTextConfirmPasswordEditProfile.isEnabled = false
            }
        }
    }

    private fun onClickShowPass() {
        if(showPass1){
            showPass1=false
            binding.editTextOldPasswordEditProfile.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHideOldPasswordEditProfile.text = "SHOW"
        }else{
            showPass1=true
            binding.editTextOldPasswordEditProfile.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHideOldPasswordEditProfile.text = "HIDE"
        }
    }

    private fun onClickShowPass2() {
        if(showPass2){
            showPass1=false
            binding.editTextNewPasswordEditProfile.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHideNewPasswordEditProfile.text = "SHOW"
        }else{
            showPass2=true
            binding.editTextNewPasswordEditProfile.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHideNewPasswordEditProfile.text = "HIDE"
        }
    }

    private fun onClickShowPass3() {
        if(showPass3){
            showPass3=false
            binding.editTextConfirmPasswordEditProfile.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHideConfirmPasswordEditProfile.text = "SHOW"
        }else{
            showPass3=true
            binding.editTextConfirmPasswordEditProfile.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHideConfirmPasswordEditProfile.text = "HIDE"
        }
    }
    private fun onClickSave() {
        if(binding.checkBoxPersonalInfo.isChecked){
            savePersonalInfor()
        }
        if(binding.checkBoxChangePassword.isChecked){
            changePassword()
        }
    }
    private fun savePersonalInfor(){
        val username = binding.editTextUsernameEditProfile.text.toString().trim()
        val fullname = binding.editTextFullnameEditProfile.text.toString().trim()
        val height = binding.editTextHeightEditProfile.text.toString().trim()
        val weight = binding.editTextWeightEditProfile.text.toString().trim()
        if(viewModel.checkInfoUser(username, user.getPassword(), user.getPassword(), fullname, height, weight)){
            val userNew = User(username, user.getPassword(), fullname, sex, height.toInt(), weight.toInt(), user.getdistanceGoal(), user.getAvartar())
            userNew.setId(user.getId())
            viewModel.updateUser(userNew)
            viewModel.writePersonalDataToSharedPref(userNew)
            this.finish()
        }else{
            Toast.makeText(this@EditProfileActivity, "Please enter your information to update account !", Toast.LENGTH_LONG).show()
        }
    }
    private fun changePassword(){
        val oldPassword = binding.editTextOldPasswordEditProfile.text.toString().trim()
        val newPassword = binding.editTextNewPasswordEditProfile.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPasswordEditProfile.text.toString().trim()
        if(!viewModel.checkSamePassword(user.getPassword(), oldPassword)){
            Toast.makeText(this@EditProfileActivity, "Old password not correct !", Toast.LENGTH_LONG).show()
        }
        if(!viewModel.checkPassword(newPassword) || !viewModel.checkPassword(confirmPassword)){
            Toast.makeText(this@EditProfileActivity, "New password must have length greater than 5 !", Toast.LENGTH_LONG).show()
        }
        if(newPassword!=confirmPassword){
            Toast.makeText(this@EditProfileActivity, "Please enter new password or confirm password correctly !", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserData() {
        val bundle = intent?.extras
        user = bundle?.getSerializable(Constants.EDIT_USER) as User
        user.let {
            binding.editTextUsernameEditProfile.setText(it.getUsername())
            binding.editTextFullnameEditProfile.setText(it.getFullname())
            binding.editTextHeightEditProfile.setText(it.getHeight().toString())
            binding.editTextWeightEditProfile.setText(it.getWeight().toString())
            binding.spinnerGioiTinhEditProfile.setSelection(if(it.getSex()=="Male") 0 else if (it.getSex()=="Female") 1 else 2)
        }
    }

    private fun setUpSpinnerSex() {
        val spinnerAdapter = ArrayAdapter(this@EditProfileActivity,
            android.R.layout.simple_list_item_1,
            resources.getStringArray(R.array.spinner_sex))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerGioiTinhEditProfile.adapter = spinnerAdapter
        binding.spinnerGioiTinhEditProfile.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                sex = parent?.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun onClickCancel() {
        finish()
    }
}