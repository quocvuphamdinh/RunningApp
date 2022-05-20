package vu.pham.runningappseminar.ui.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.ui.activities.MainActivity
import vu.pham.runningappseminar.databinding.FragmentProfileBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.ui.utils.LoadingDialog
import vu.pham.runningappseminar.utils.*
import vu.pham.runningappseminar.viewmodels.ProfileViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ProfileViewModelFactory
import java.io.ByteArrayOutputStream
import kotlin.math.round

class ProfileFragment : Fragment() {
    private lateinit var binding:FragmentProfileBinding
    private lateinit var mActivityResult:ActivityResultLauncher<Intent>
    private lateinit var loadingDialog: LoadingDialog

    private val viewModel : ProfileViewModel by viewModels{
        ProfileViewModelFactory((activity?.application as RunApplication).repository,  activity?.application as RunApplication)
    }

    private lateinit var userLocal:User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingDialog = LoadingDialog()
        setUpInfoUserProfile()

        binding.imageViewEditProfile.setOnClickListener {
            goToEditProfile()
        }

        binding.cardViewHistoryRun.setOnClickListener {
            goToHistoryRun()
        }

        binding.cardViewLogout.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                clickLogout()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }

        binding.cardViewSyncData.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                doSyncData()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }

        binding.imageViewAvartarProfileFragment.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                clickRequestImage()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }

        binding.cardViewChangePassword.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                goToChangePassword()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }

        subscribeToObservers()

        mActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult(),
            ActivityResultCallback<ActivityResult>{ result ->
                val intentData = result.data
                if (intentData!=null){
                    val uri = intentData.data
                    val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, uri)
                    uploadImageToServer(bitmap)
                }
            }
        )

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        }
    }

    private fun showLoadingDialog(){
        loadingDialog.show(parentFragmentManager, Constants.LOADING_DIALOG_TAG)
    }

    private fun goToChangePassword() {
        viewModel.clearToast()
        val bundle = Bundle()
        bundle.putSerializable(Constants.CHANGE_PASSWORD, userLocal)
        findNavController().navigate(R.id.action_profileFragment_to_changePasswordFragment, bundle)
    }

    private fun uploadImageToServer(bitmap: Bitmap) {
        val storageRef = viewModel.getFirebaseStorage()?.reference
        val nameHinh="image-"+userLocal.getId()
        val nameHinh2= "$nameHinh.png"
        val mountainsRef = storageRef?.child(nameHinh2)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainsRef?.putBytes(data)
        uploadTask?.addOnFailureListener {
            Toast.makeText(context, "Error uploaded avatar !", Toast.LENGTH_SHORT).show()
            Log.d("hivu", it.toString())
        }?.addOnSuccessListener { _ ->
            Toast.makeText(context, "Uploaded avatar successfully !", Toast.LENGTH_SHORT).show()
            mountainsRef.downloadUrl.addOnSuccessListener { uri ->
                userLocal.setAvartar(uri.toString())
                viewModel.updateUser(userLocal)
            }
        }
    }

    private fun clickRequestImage(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (requireActivity().checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED
                && requireActivity().checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                openGallery()
            }else{
                val permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                requestPermissions(permission, Constants.REQUEST_CODE_IMAGE_UPLOAD)
            }
        }else{
            openGallery()
        }
    }

    private fun openGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        mActivityResult.launch(Intent.createChooser(intent, "Choose your image"))
    }

    private fun doSyncData() {
        viewModel.syncData(userLocal.getId())
    }

    private fun clickLogout() {
        showLoadingDialog()
        viewModel.logOut(userLocal.getId())
    }

    private fun bindUserDataToView(userBind: User?){
        binding.textViewFullnameProfileFragment.text = userBind?.getFullname()
        binding.textViewSexProfile.text = userBind?.getSex()
        binding.textViewHeight.text = "${userBind?.getHeight()} cm"
        binding.textViewWeight.text = "${userBind?.getWeight()} kg"
        binding.textViewDistanceGoalProfileFragment.text = "${userBind?.getdistanceGoal()} km"
        if(CheckConnection.haveNetworkConnection(requireContext())){
            if(userBind?.getAvartar()?.isNotEmpty() == true){
                Picasso.get().load(userBind.getAvartar())
                    .placeholder(R.drawable.default_avartar)
                    .into(binding.imageViewAvartarProfileFragment)
            }
        }
    }

    private fun setUpUserProfileFromServer(){
        viewModel.getUserLiveData(userLocal.getUsername(), userLocal.getPassword())
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.getUserLiveData(userLocal.getUsername(), userLocal.getPassword())
           bindUserDataToView(it)
        })
    }

    private fun setUpInfoUserProfile() {
        userLocal = viewModel.getUserFromSharedPref()!!
        if(CheckConnection.haveNetworkConnection(requireContext())){
            setUpUserProfileFromServer()
        }else{
            bindUserDataToView(userLocal)
        }
    }

    private fun subscribeToObservers() {
        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.success.observe(viewLifecycleOwner, Observer {
            if(it){
                val i = Intent(context, MainActivity::class.java)
                i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(i)
                activity?.overridePendingTransition(0,0)
            }
            loadingDialog.dismissDialog()
        })

        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewTotalDistanceProfile.text = (it/1000f).toString()
            }
        })
        viewModel.totalTimeInMillies.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewTotalHoursProfile.text = TrackingUtil.getFormattedTimer2(it, 1)
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewTotalCaloriesBurnedProfile.text = it.toString()
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewTotalAvgSpeedProfile.text = (round(it*10)/10f).toString()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        userLocal = viewModel.getUserFromSharedPref()!!
    }

    private fun goToHistoryRun() {
        viewModel.clearToast()
       findNavController().navigate(R.id.action_profileFragment_to_historyRunFragment)
    }

    private fun goToEditProfile() {
        viewModel.clearToast()
        val bundle = Bundle()
        bundle.putSerializable(Constants.EDIT_USER, userLocal)
        findNavController().navigate(R.id.action_profileFragment_to_editProfileFragment, bundle)
    }
}