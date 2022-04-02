package vu.pham.runningappseminar.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.SetMyGoalActivity
import vu.pham.runningappseminar.adapter.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.adapter.RecyclerViewRecentActivitiesAdapter
import vu.pham.runningappseminar.databinding.HomeFragmentBinding
import vu.pham.runningappseminar.model.Data
import vu.pham.runningappseminar.model.User
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory


class HomeFragment : Fragment() {
    private lateinit var adapterTodayTraining:RecyclerViewActivityAdapter
    private lateinit var adapterRecentActivities:RecyclerViewRecentActivitiesAdapter
    private lateinit var binding:HomeFragmentBinding

    private var user:User?=null
    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    private var resultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val goalValue = data?.getIntExtra(Constants.INTENT_SET_MYGOAL, 1000)
            goalValue?.let {
                val user = viewModel.getUserFromSharedPref()
                user?.setdistanceGoal(it.toLong())
                viewModel.writePersonalDataToSharedPref(user!!)
                viewModel.updateUser(user)
            }
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.home_fragment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initActivityList()
        initRecentActivities()
        initUserInfo()

        binding.imageViewSetMyGoal.setOnClickListener {
            clickGoToSetMyGoal()
        }
        subscribeToObservers()
    }

    private fun initUserInfo() {
        user = viewModel.getUserFromSharedPref()
        if(context?.let { CheckConnection.haveNetworkConnection(it) } == true){
            user?.getUsername()?.let {
                viewModel.getUserLiveData(it, user!!.getPassword())
                viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
                    user = it
                    viewModel.getUserLiveData(user!!.getUsername(), user!!.getPassword())
                    bindDataToDistanceGoalView(user)
                })
            }
        }else{
            bindDataToDistanceGoalView(user)
        }
    }
    private fun bindDataToDistanceGoalView(user: User?){
        binding.textViewWeeklyGoal.text = "Weekly goal ${user?.getdistanceGoal()} km"
        binding.progressBar.max = user?.getdistanceGoal()?.toInt() ?: 0
        val nameUser = "Let's go "
        val text = user?.getFullname()
        val text2 = nameUser+text
        val spannable: Spannable = SpannableString(text2)

        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.grey_200)), nameUser.length, (text + nameUser).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textViewWellcome.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    private fun subscribeToObservers(){
        viewModel.totalDistanceWeekly.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewNumberDistance.text = (it/ 1000f).toString()
                binding.progressBar.progress = (it/ 1000f).toInt()
            }
        })

        viewModel.totalCaloriesBurnedToday.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewCaloriedBurnHomeFragment.text = it.toString()
            }
        })

        viewModel.totalTimeInMilliesToday.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewRunInTimeMilliesHomeFragment.text = it.toString()
            }
        })

        viewModel.totalAvgSpeedToday.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewAvgSpeedHomeFragment.text = it.toString()
            }
        })

        viewModel.runCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewRunCountHomeFragment.text = it.toString()
            }
        })
        viewModel.maxDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewMaxDistance.text = "${(it/1000f)} km"
            }
        })
        viewModel.maxTimeInMillies.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewMaxTimeInMillies.text = TrackingUtil.getFormattedTimer(it)
            }
        })
        viewModel.maxCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewMaxCaloriesBurned.text = "$it kcal"
            }
        })
    }

    private fun clickGoToSetMyGoal() {
        val intent = Intent(context, SetMyGoalActivity::class.java)
        val bundle = Bundle()
        bundle.putLong(Constants.INIT_SET_MYGOAL, user?.getdistanceGoal()!!)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    private fun initRecentActivities() {
        adapterRecentActivities = RecyclerViewRecentActivitiesAdapter()
        adapterRecentActivities.setData(Data.userActivityList)
        binding.recyclerViewRecentActiviesHomeFragment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRecentActiviesHomeFragment.adapter = adapterRecentActivities
        binding.recyclerViewRecentActiviesHomeFragment.setHasFixedSize(true)
        binding.recyclerViewRecentActiviesHomeFragment.isNestedScrollingEnabled = false
    }

    private fun initActivityList() {
        adapterTodayTraining = RecyclerViewActivityAdapter()
        adapterTodayTraining.setData(Data.ActivityList, R.layout.activity_item_row)
        binding.recyclerViewActivityHomeFragment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActivityHomeFragment.adapter = adapterTodayTraining
        binding.recyclerViewActivityHomeFragment.setHasFixedSize(true)
    }
}