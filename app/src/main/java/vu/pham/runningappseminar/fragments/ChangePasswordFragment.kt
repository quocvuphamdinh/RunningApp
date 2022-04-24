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
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentChangePasswordBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ChangePasswordViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ChangePasswordViewModelFactory

class ChangePasswordFragment : Fragment() {
    private lateinit var binding : FragmentChangePasswordBinding
    private var showPass1=false
    private var showPass2=false
    private var showPass3=false
    private var user: User = User()
    private val viewModel : ChangePasswordViewModel by viewModels{
        ChangePasswordViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentChangePasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserData()

        binding.textViewShowHideOldPasswordChangepassword.setOnClickListener {
            onClickShowPass()
        }

        binding.textViewShowHideNewPasswordChangepassword.setOnClickListener {
            onClickShowPass2()
        }

        binding.textViewShowHideConfirmPasswordChangepassword.setOnClickListener {
            onClickShowPass3()
        }

        binding.textViewCancelChangePassword.setOnClickListener {
            findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
        }

        observeError()

        binding.textViewSaveChangePassword.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                changePassword()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun observeError() {
        viewModel.errEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun onClickShowPass() {
        if(showPass1){
            showPass1=false
            binding.editTextOldPasswordChangepassword.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHideOldPasswordChangepassword.text = "SHOW"
        }else{
            showPass1=true
            binding.editTextOldPasswordChangepassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHideOldPasswordChangepassword.text = "HIDE"
        }
    }

    private fun onClickShowPass2() {
        if(showPass2){
            showPass1=false
            binding.editTextNewPasswordChangepassword.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHideNewPasswordChangepassword.text = "SHOW"
        }else{
            showPass2=true
            binding.editTextNewPasswordChangepassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHideNewPasswordChangepassword.text = "HIDE"
        }
    }

    private fun onClickShowPass3() {
        if(showPass3){
            showPass3=false
            binding.editTextConfirmPasswordChangepassword.transformationMethod= PasswordTransformationMethod.getInstance()
            binding.textViewShowHideConfirmPasswordChangepassword.text = "SHOW"
        }else{
            showPass3=true
            binding.editTextConfirmPasswordChangepassword.transformationMethod= HideReturnsTransformationMethod.getInstance()
            binding.textViewShowHideConfirmPasswordChangepassword.text = "HIDE"
        }
    }

    private fun changePassword(){
        val oldPassword = binding.editTextOldPasswordChangepassword.text.toString().trim()
        val newPassword = binding.editTextNewPasswordChangepassword.text.toString().trim()
        val confirmPassword = binding.editTextConfirmPasswordChangepassword.text.toString().trim()
        if(!viewModel.checkSamePassword(user.getPassword(), oldPassword)){
            Toast.makeText(requireContext(), "Old password not correct !", Toast.LENGTH_LONG).show()
        } else if(!viewModel.checkPassword(newPassword) || !viewModel.checkPassword(confirmPassword)){
            Toast.makeText(requireContext(), "New password must have length greater than 5 !", Toast.LENGTH_LONG).show()
        } else if(newPassword!=confirmPassword){
            Toast.makeText(requireContext(), "Please enter new password or confirm password correctly !", Toast.LENGTH_LONG).show()
        }else{
            user.setPassword(newPassword)
            viewModel.updateUser(user)
            findNavController().navigate(R.id.action_changePasswordFragment_to_profileFragment)
        }
    }

    private fun getUserData() {
        val bundle = arguments
        user = bundle?.getSerializable(Constants.CHANGE_PASSWORD) as User
    }
}