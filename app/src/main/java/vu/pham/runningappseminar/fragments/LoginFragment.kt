package vu.pham.runningappseminar.fragments

import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import vu.pham.runningappseminar.activities.MainActivity
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.databinding.FragmentLoginBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.LoadingDialog
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.LoginViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.LoginViewModelFactory

class LoginFragment : Fragment() {

    private lateinit var binding:FragmentLoginBinding
    private lateinit var loadingDialog : LoadingDialog
    private var showPass = false
    private val viewModel : LoginViewModel by viewModels{
        LoginViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity as MainActivity)
        binding.textViewBackWelComeScreen2.setOnClickListener {
            goBack()
        }
        binding.textViewForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }
        binding.textViewShowHidePassword.setOnClickListener {
            onClickShowPass()
        }
        binding.buttonSignIn.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                checkUserInServer()
            }else{
                Toast.makeText(context, "your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun writePersonalDataToSharedPref(user: User){
        viewModel.writePersonalDataToSharedPref(user)
    }

    private fun checkUserInServer() {
        val username = binding.editTextUsernameLogin.text.toString().trim()
        val password = binding.editTextPasswordLogin.text.toString().trim()
        loadingDialog.startLoadingDialog()
        viewModel.getUser(username, password).enqueue(object : Callback<User?> {
            override fun onResponse(call: Call<User?>, response: Response<User?>) {
                val user = response.body()
                if(viewModel.checkSameUser(username, password, user)){
                    viewModel.getAllRunFromRemote(user?.getId()!!).enqueue(object :
                        Callback<List<Run>> {
                        override fun onResponse(call: Call<List<Run>>, response: Response<List<Run>>) {
                            lifecycleScope.launch {
                                val listRun = response.body()
                                for (run in listRun!!){
                                    viewModel.insertRunLocal(run)
                                }
                                writePersonalDataToSharedPref(user)
                                loadingDialog.dismissDialog()
                                Toast.makeText(context, "Login success !", Toast.LENGTH_LONG).show()
                                goToHomePage()
                            }
                        }
                        override fun onFailure(call: Call<List<Run>>, t: Throwable) {
                            Toast.makeText(context, "Error: $t !", Toast.LENGTH_LONG).show()
                            loadingDialog.dismissDialog()
                        }
                    })
                }else{
                    Toast.makeText(context, "Login failed !", Toast.LENGTH_LONG).show()
                    loadingDialog.dismissDialog()
                }
            }

            override fun onFailure(call: Call<User?>, t: Throwable) {
                Toast.makeText(context, "Error: $t !", Toast.LENGTH_LONG).show()
                loadingDialog.dismissDialog()
            }
        })
    }

    private fun goToHomePage(){
        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
    }
    private fun goBack() {
        findNavController().navigate(R.id.action_loginFragment_to_welcomeFragment)
    }

    private fun onClickShowPass() {
        if(showPass){
            showPass=false
            binding.editTextPasswordLogin.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHidePassword.text = "SHOW"
        }else{
            showPass=true
            binding.editTextPasswordLogin.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHidePassword.text = "HIDE"
        }
    }
}