package vu.pham.runningappseminar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentEditProfileBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.EditProfileViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.EditProfileViewModelFactory

class EditProfileFragment : Fragment() {
    private lateinit var binding : FragmentEditProfileBinding
    var sex = ""
    private var user: User = User()

    private val viewModel : EditProfileViewModel by viewModels{
        EditProfileViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpSpinnerSex()
        getUserData()

        observeError()

        binding.textViewSaveEditProfile.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                savePersonalInfor()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
        binding.textViewCancelEditProfile.setOnClickListener {
            onClickCancel()
        }
    }

    private fun observeError() {
        viewModel.errEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun savePersonalInfor(){
        val username = binding.editTextUsernameEditProfile.text.toString().trim()
        val fullname = binding.editTextFullnameEditProfile.text.toString().trim()
        val height = binding.editTextHeightEditProfile.text.toString().trim()
        val weight = binding.editTextWeightEditProfile.text.toString().trim()
        if(viewModel.checkInfoUser(username, fullname, height, weight)){
            val userNew = User(username, user.getPassword(), fullname, sex, height.toInt(), weight.toInt(), user.getdistanceGoal(), user.getAvartar())
            userNew.setId(user.getId())
            viewModel.updateUser(userNew)
            findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
        }else{
            Toast.makeText(requireContext(), "Please enter your information to update account !", Toast.LENGTH_LONG).show()
        }
    }

    private fun getUserData() {
        val bundle = arguments
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
        val spinnerAdapter = ArrayAdapter(requireContext(),
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
        findNavController().navigate(R.id.action_editProfileFragment_to_profileFragment)
    }
}