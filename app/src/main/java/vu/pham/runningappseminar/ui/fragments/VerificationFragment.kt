package vu.pham.runningappseminar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.wynsbin.vciv.VerificationCodeInputView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentVerificationBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.VerificationViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.VerificationViewModelFactory

class VerificationFragment: Fragment() {
    private lateinit var binding: FragmentVerificationBinding
    private val viewModel: VerificationViewModel by viewModels {
        VerificationViewModelFactory((activity?.application as RunApplication).repository)
    }
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog() }
    private var user: User? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentVerificationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getUserData()
        subcribeToObservers()
        binding.verificationCode.setOnInputListener(object : VerificationCodeInputView.OnInputListener{
            override fun onComplete(code: String?) {
                code?.let {
                    loadingDialog.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
                    viewModel.checkOTPCode(it, user!!)
                }
            }

            override fun onInput() {
            }
        })
        binding.textResendCode.setOnClickListener {
            viewModel.checkEmailExists(user?.getUsername()!!)
        }
        binding.imageViewBackInVerifi.setOnClickListener {
            findNavController().navigate(R.id.action_verificationFragment_to_signUpFragment)
        }
    }

    private fun getUserData() {
        val bundle = arguments
        bundle?.let {
            user = it.getSerializable("userSignUp") as User
            binding.tv2.text = "We have sent the OTP code to ${user?.getUsername()}, please check your gmail and enter the OTP code below to verify your account"
        }
    }

    private fun subcribeToObservers() {
        viewModel.toast.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
        viewModel.success.observe(viewLifecycleOwner, Observer {
            loadingDialog.dismissDialog()
            if(it){
                findNavController().navigate(R.id.action_verificationFragment_to_loginFragment)
            }else{
                findNavController().navigate(R.id.action_verificationFragment_to_signUpFragment)
            }
        })
    }
}