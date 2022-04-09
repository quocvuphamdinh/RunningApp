package vu.pham.runningappseminar.fragments

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
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activities.EditProfileActivity
import vu.pham.runningappseminar.activities.HistoryRunActivity
import vu.pham.runningappseminar.activities.MainActivity
import vu.pham.runningappseminar.databinding.ProfileFragmentBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory
import java.io.ByteArrayOutputStream
import kotlin.math.round

class ProfileFragment : Fragment() {
    private lateinit var binding:ProfileFragmentBinding
    private lateinit var mActivityResult:ActivityResultLauncher<Intent>

    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    private lateinit var userLocal:User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.profile_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpInfoUserProfile()

        binding.imageViewEditProfile.setOnClickListener {
            goToEditProfile()
        }

        binding.cardViewHistoryRun.setOnClickListener {
            goToHistoryRun()
        }

        binding.cardViewLogout.setOnClickListener {
            clickLogout()
        }

        binding.cardViewSyncData.setOnClickListener {
            doSyncData()
        }

        binding.imageViewAvartarProfileFragment.setOnClickListener {
            clickRequestImage()
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
    }

    private fun uploadImageToServer(bitmap: Bitmap) {
        val storageRef = viewModel.getFirebaseStorage().reference
        val nameHinh="image-"+userLocal.getId()
        val nameHinh2= "$nameHinh.png"
        val mountainsRef = storageRef.child(nameHinh2)
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val data = baos.toByteArray()
        val uploadTask = mountainsRef.putBytes(data)
        uploadTask.addOnFailureListener {
            Toast.makeText(context, "Error uploaded avatar !", Toast.LENGTH_SHORT).show()
            Log.d("hivu", it.toString())
        }.addOnSuccessListener { taskSnapshot ->
            Toast.makeText(context, "Uploaded avatar successfully !", Toast.LENGTH_SHORT).show()
        }
        mountainsRef.downloadUrl.addOnSuccessListener { uri ->
            userLocal.setAvartar(uri.toString())
            viewModel.updateUser(userLocal)
            viewModel.writePersonalDataToSharedPref(userLocal)
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
        lifecycleScope.launch {
            val listRun = viewModel.getAllRunFromLocal()
            for (run in listRun){
                viewModel.insertRunRemote(run, userLocal.getId(), -1L)
            }
            Toast.makeText(context, "Sync successfully !", Toast.LENGTH_LONG).show()
        }
    }

    private fun clickLogout() {
        lifecycleScope.launch {
            val listRun = viewModel.getAllRunFromLocal()
            for (run in listRun){
                viewModel.insertRunRemote(run, userLocal.getId(), -1L)
            }
            viewModel.deleteAllRun()
            val intent = Intent(context, MainActivity::class.java)
            viewModel.removePersonalDataFromSharedPref()
            activity?.finish()
            startActivity(intent)
        }
    }

    private fun bindUserDataToView(userBind: User?){
        binding.textViewFullnameProfileFragment.text = userBind?.getFullname()
        binding.textViewSexProfile.text = userBind?.getSex()
        binding.textViewHeight.text = "${userBind?.getHeight()} cm"
        binding.textViewWeight.text = "${userBind?.getWeight()} kg"
        binding.textViewDistanceGoalProfileFragment.text = "${userBind?.getdistanceGoal()} km"
        if(userBind?.getAvartar()?.isNotEmpty() == true){
            Picasso.get().load(userBind.getAvartar())
                .placeholder(R.drawable.default_avartar)
                .into(binding.imageViewAvartarProfileFragment)
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
        if(context?.let { CheckConnection.haveNetworkConnection(it) } == true){
            setUpUserProfileFromServer()
        }else{
            bindUserDataToView(userLocal)
        }
    }

    private fun subscribeToObservers() {
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
        val intent = Intent(context, HistoryRunActivity::class.java)
        startActivity(intent)
    }

    private fun goToEditProfile() {
        val intent = Intent(context, EditProfileActivity::class.java)
        val bundle = Bundle()
        bundle.putSerializable(Constants.EDIT_USER, userLocal)
        intent.putExtras(bundle)
        startActivity(intent)
    }
}