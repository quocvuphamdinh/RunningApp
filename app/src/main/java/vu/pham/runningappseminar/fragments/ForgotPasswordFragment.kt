package vu.pham.runningappseminar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activities.MainActivity
import vu.pham.runningappseminar.databinding.FragmentForgotPasswordBinding
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.LoadingDialog
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ForgotPasswordViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ForgotPasswordViewModelFactory

class ForgotPasswordFragment : Fragment() {

    private lateinit var binding:FragmentForgotPasswordBinding
    private lateinit var loadingDialog: LoadingDialog

    private val viewModel : ForgotPasswordViewModel by viewModels{
        ForgotPasswordViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog(activity as MainActivity)

        subcribeToObservers()

        binding.imageViewBackInForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }

        binding.buttonResetPassword.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                doResetPassword()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun subcribeToObservers() {
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.successResetPassword.observe(viewLifecycleOwner, Observer {
            if(it){
                loadingDialog.dismissDialog()
                findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
            }
        })
    }

    private fun doResetPassword() {
        val userName = binding.editTextResetPassword.text.toString().trim()
        val success = viewModel.checkEmailIsValid(userName)
        if(success){
            loadingDialog.startLoadingDialog()
            viewModel.resetPassword(userName)
        }else{
            Toast.makeText(context, "Email is not correct format !", Toast.LENGTH_SHORT).show()
        }
    }
}