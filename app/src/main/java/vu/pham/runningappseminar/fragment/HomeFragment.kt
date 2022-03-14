package vu.pham.runningappseminar.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activity.SetMyGoalActivity
import vu.pham.runningappseminar.adapter.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.adapter.RecyclerViewRecentActivitiesAdapter
import vu.pham.runningappseminar.model.Data
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory

class HomeFragment : Fragment() {
    private lateinit var recyclerViewTodayTraining:RecyclerView
    private lateinit var recyclerViewRecentActivities:RecyclerView
    private lateinit var adapterTodayTraining:RecyclerViewActivityAdapter
    private lateinit var adapterRecentActivities:RecyclerViewRecentActivitiesAdapter
    private lateinit var imgSetMyGoal:ImageView
    private lateinit var txtDistanceWeekly:TextView
    private lateinit var txtWeeklyGoal:TextView
    private lateinit var progressBar:ProgressBar
    private lateinit var txtTotalCaloriesBurnedToday:TextView
    private lateinit var txtTotalTimeInMilliesToday:TextView
    private lateinit var txtAvgSpeedToday:TextView
    private lateinit var txtRunCountToday:TextView
    private lateinit var txtMaxDistance:TextView
    private lateinit var txtMaxTimeInMillies:TextView
    private lateinit var txtMaxCaloriesBurned:TextView
    private var resultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val goalValue = data?.getIntExtra(Constants.INTENT_SET_MYGOAL, 1000)
            goalValue?.let {
                progressBar.max = it
                txtWeeklyGoal.text = "Weekly goal $it km"
            }
        }
    }

    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.home_fragment, container, false)
        anhXa(view)
        initActivityList()
        initRecentActivities()

        imgSetMyGoal.setOnClickListener {
            clickGoToSetMyGoal()
        }
        subscribeToObservers()
        return view
    }

    private fun subscribeToObservers(){
        viewModel.totalDistanceWeekly.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtDistanceWeekly.text = (it/ 1000f).toString()
                progressBar.progress = (it/ 1000f).toInt()
            }
        })

        viewModel.totalCaloriesBurnedToday.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtTotalCaloriesBurnedToday.text = it.toString()
            }
        })

        viewModel.totalTimeInMilliesToday.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtTotalTimeInMilliesToday.text = it.toString()
            }
        })

        viewModel.totalAvgSpeedToday.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtAvgSpeedToday.text = it.toString()
            }
        })

        viewModel.runCount.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtRunCountToday.text = it.toString()
            }
        })
        viewModel.maxDistance.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtMaxDistance.text = "${(it/1000f)} km"
            }
        })
        viewModel.maxTimeInMillies.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtMaxTimeInMillies.text = TrackingUtil.getFormattedTimer(it)
            }
        })
        viewModel.maxCaloriesBurned.observe(viewLifecycleOwner, Observer {
            it?.let {
                txtMaxCaloriesBurned.text = "$it kcal"
            }
        })
    }

    private fun clickGoToSetMyGoal() {
        val intent = Intent(context, SetMyGoalActivity::class.java)
        resultLauncher.launch(intent)
    }

    private fun initRecentActivities() {
        adapterRecentActivities = RecyclerViewRecentActivitiesAdapter()
        adapterRecentActivities.setData(Data.userActivityList)
        recyclerViewRecentActivities.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerViewRecentActivities.adapter = adapterRecentActivities
        recyclerViewRecentActivities.setHasFixedSize(true)
        recyclerViewRecentActivities.isNestedScrollingEnabled = false
    }

    private fun initActivityList() {
        adapterTodayTraining = RecyclerViewActivityAdapter()
        adapterTodayTraining.setData(Data.ActivityList, R.layout.activity_item_row)
        recyclerViewTodayTraining.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewTodayTraining.adapter = adapterTodayTraining
        recyclerViewTodayTraining.setHasFixedSize(true)
    }

    private fun anhXa(view: View) {
        recyclerViewTodayTraining = view.findViewById(R.id.recyclerViewActivityHomeFragment)
        recyclerViewRecentActivities = view.findViewById(R.id.recyclerViewRecentActiviesHomeFragment)
        imgSetMyGoal = view.findViewById(R.id.imageViewSetMyGoal)
        txtDistanceWeekly = view.findViewById(R.id.textViewNumberDistance)
        progressBar = view.findViewById(R.id.progressBar)
        txtWeeklyGoal = view.findViewById(R.id.textViewWeeklyGoal)
        txtTotalCaloriesBurnedToday = view.findViewById(R.id.textViewCaloriedBurnHomeFragment)
        txtTotalTimeInMilliesToday = view.findViewById(R.id.textViewRunInTimeMilliesHomeFragment)
        txtAvgSpeedToday = view.findViewById(R.id.textViewAvgSpeedHomeFragment)
        txtRunCountToday = view.findViewById(R.id.textViewRunCountHomeFragment)
        txtMaxDistance = view.findViewById(R.id.textViewMaxDistance)
        txtMaxTimeInMillies = view.findViewById(R.id.textViewMaxTimeInMillies)
        txtMaxCaloriesBurned = view.findViewById(R.id.textViewMaxCaloriesBurned)
    }
}