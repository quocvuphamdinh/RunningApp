package vu.pham.runningappseminar.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.ui.activities.HomeActivity
import vu.pham.runningappseminar.databinding.FragmentWelcomeBinding
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.WelcomeViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.WelcomeViewModelFactory

class WelcomeFragment : Fragment() {
    private lateinit var binding:FragmentWelcomeBinding
    private var currentNavController: LiveData<NavController>? = null
    private val viewModel : WelcomeViewModel by viewModels{
        WelcomeViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val isFirstAppOpen = viewModel.getFirstTimeToogle()
        if(!isFirstAppOpen){
            goToHomePage()
        }

        goToSignInScreen()
        goToSignUpScreen()
    }
    private fun goToHomePage(){
        startActivity(Intent(context, HomeActivity::class.java))
        activity?.overridePendingTransition(0,0)
    }
    private fun goToSignUpScreen() {
        binding.buttonSignupWelcome.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }
    }

    private fun goToSignInScreen() {
        binding.buttonSignInWelcome.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }
    }
}