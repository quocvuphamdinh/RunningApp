package vu.pham.runningappseminar.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.EditProfileActivity
import vu.pham.runningappseminar.activity.HistoryRunActivity
import vu.pham.runningappseminar.activity.MainActivity
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory
import kotlin.math.round

class ProfileFragment : Fragment() {
    private lateinit var imgEditProfile:ImageView
    private lateinit var txtFullName:TextView
    private lateinit var txtSex:TextView
    private lateinit var txtHeight:TextView
    private lateinit var txtWeight:TextView
    private lateinit var cardViewRunHistory:CardView
    private lateinit var cardViewLogout:CardView
    private lateinit var txtTotalDistance:TextView
    private lateinit var txtTotalHours:TextView
    private lateinit var txtCaloriesBurned:TextView
    private lateinit var txtAvgSpeed:TextView

    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    private lateinit var userLocal:User

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.profile_fragment, container, false)

        anhXa(view)

        setUpInfoUserProfile()
        setUpInfoLiveDataUserProfile()

        imgEditProfile.setOnClickListener {
            goToEditProfile()
        }

        cardViewRunHistory.setOnClickListener {
            goToHistoryRun()
        }

        cardViewLogout.setOnClickListener {
            clickLogout()
        }

        subscribeToObservers()

        return view
    }

    private fun clickLogout() {
        val intent = Intent(context, MainActivity::class.java)
        viewModel.removePersonalDataFromSharedPref()
        activity?.finish()
        startActivity(intent)
    }

    private fun bindUserDataToView(userBind: User?){
        if(userBind?.getAvartar()?.isNotEmpty() == true){
            Picasso.get().load(userBind.getAvartar()).into(imgEditProfile)
        }
        txtFullName.text = userBind?.getFullname()
        txtSex.text = userBind?.getSex()
        txtHeight.text = "${userBind?.getHeight()} cm"
        txtWeight.text = "${userBind?.getWeight()} kg"
    }

    private fun setUpInfoLiveDataUserProfile(){
        viewModel.getUserLiveData(userLocal.getUsername(), userLocal.getPassword())
        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            viewModel.getUserLiveData(userLocal.getUsername(), userLocal.getPassword())
           bindUserDataToView(it)
        })
    }
    private fun setUpInfoUserProfile() {
        userLocal = viewModel.getUserFromSharedPref()!!
        //bindUserDataToView(userLocal)
    }

    private fun subscribeToObservers() {
        viewModel.totalDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtTotalDistance.text = (it/1000f).toString()
            }
        })
        viewModel.totalTimeInMillies.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtTotalHours.text = TrackingUtil.getFormattedTimer2(it, 1)
            }
        })
        viewModel.totalCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtCaloriesBurned.text = it.toString()
            }
        })
        viewModel.totalAvgSpeed.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtAvgSpeed.text = (round(it*10)/10f).toString()
            }
        })
    }

    private fun goToHistoryRun() {
        val intent = Intent(context, HistoryRunActivity::class.java)
        startActivity(intent)
    }

    private fun goToEditProfile() {
        val intent = Intent(context, EditProfileActivity::class.java)
        startActivity(intent)
    }

    private fun anhXa(view: View) {
        imgEditProfile = view.findViewById(R.id.imageViewEditProfile)
        cardViewRunHistory = view.findViewById(R.id.cardViewHistoryRun)
        txtTotalDistance = view.findViewById(R.id.textViewTotalDistanceProfile)
        txtTotalHours = view.findViewById(R.id.textViewTotalHoursProfile)
        txtCaloriesBurned = view.findViewById(R.id.textViewTotalCaloriesBurnedProfile)
        txtAvgSpeed = view.findViewById(R.id.textViewTotalAvgSpeedProfile)
        txtFullName = view.findViewById(R.id.textViewFullnameProfileFragment)
        txtSex = view.findViewById(R.id.textViewSexProfile)
        txtHeight = view.findViewById(R.id.textViewHeight)
        txtWeight = view.findViewById(R.id.textViewWeight)
        cardViewLogout = view.findViewById(R.id.cardViewLogout)
    }
}