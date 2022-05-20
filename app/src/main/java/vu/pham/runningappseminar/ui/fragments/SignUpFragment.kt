package vu.pham.runningappseminar.ui.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.ui.activities.MainActivity
import vu.pham.runningappseminar.databinding.FragmentSignupBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.SignUpViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.SignUpViewModelFactory

class SignUpFragment : Fragment() {
    private lateinit var binding : FragmentSignupBinding
    private lateinit var adapterSpinner: ArrayAdapter<String>
    private lateinit var loadingDialog: LoadingDialog
    private var showPass = false
    private var showPass2 = false
    var sex = ""
    private val viewModel : SignUpViewModel by viewModels{
        SignUpViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSpinner()
        loadingDialog = LoadingDialog()

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
            if(CheckConnection.haveNetworkConnection(requireContext())){
                signUpAccount()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun goBack() {
        findNavController().navigate(R.id.action_signUpFragment_to_welcomeFragment)
    }

    private fun signUpAccount(){
        val username= binding.editTextUsernameSignUp.text.toString().trim()
        val password = binding.editTextPasswordSignUp.text.toString().trim()
        val password2= binding.editTextPasswordSignUp2.text.toString().trim()
        val fullname = binding.editTextFullNameSignup.text.toString().trim()
        val height = binding.editTextHeightSignup.text.toString().trim()
        val weight = binding.editTextWeightSignup.text.toString().trim()
        if(!viewModel.checkInfoUser(username, password, password2, fullname, height, weight)){
            Toast.makeText(context, "Please enter your information correctly to create account !", Toast.LENGTH_LONG).show()
        }else{
            if(!viewModel.checkSamePassword(password, password2)){
                Toast.makeText(context, "Confirm password must the same as password. Please try again !", Toast.LENGTH_LONG).show()
            }else{
                val user = User(username, password, fullname, sex, height.toInt(), weight.toInt(), 0, "")
                checkEmailAccount(user)
            }
        }
    }
    private fun showLoadingDialog(){
        loadingDialog.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
    }

    private fun checkEmailAccount(user: User){
        lifecycleScope.launch {
            showLoadingDialog()
            try {
                val userValid = viewModel.checkEmailAccount(user.getUsername())
                if (userValid.getUsername().isNotEmpty()){
                    Toast.makeText(context, "This email account has already existed !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                }else{
                    getUser(user)
                }
            }catch (e:Exception){
                Toast.makeText(requireContext(), "An error has occurred, something happens in server !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
        }
    }

    private fun checkUser(user: User){
        viewModel.getUser(user.getUsername(), user.getPassword()).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val user3 = response.body()
                if(viewModel.checkSameUser(user3?.getUsername()!!, user3.getPassword(), user)){
                    Toast.makeText(context, "Sign up success !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                    goToLogin()
                }else{
                    Toast.makeText(context, "Sign up failed !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(context, "Error: $t !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
        })
    }
    private fun getUser(user: User){
        viewModel.getUser(user.getUsername(), user.getPassword()).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val user2 = response.body()
                if(viewModel.checkSameUser(user2?.getUsername()!!, user2.getPassword(), user)){
                    Toast.makeText(context, "Email and password exist !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                }else{
                    lifecycleScope.launch {
                        try {
                            viewModel.insertUser(user)
                            checkUser(user)
                        }catch (e:Exception){
                            Toast.makeText(requireContext(), "An error has occurred, something happens in server !", Toast.LENGTH_LONG).show()
                            loadingDialog.dismissDialog()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(context, "Error: $t !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
        })
    }

    private fun goToLogin() {
        findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
    }

    private fun initSpinner(){
        adapterSpinner = object: ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(
            R.array.spinner_sex_signup)){
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