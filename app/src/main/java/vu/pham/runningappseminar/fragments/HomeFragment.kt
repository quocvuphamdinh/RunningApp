package vu.pham.runningappseminar.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.activities.SetMyGoalActivity
import vu.pham.runningappseminar.adapters.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.adapters.RecyclerViewRecentActivitiesAdapter
import vu.pham.runningappseminar.databinding.FragmentHomeBinding
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory


class HomeFragment : Fragment() {
    private lateinit var adapterTodayTraining:RecyclerViewActivityAdapter
    private lateinit var adapterRecentActivities:RecyclerViewRecentActivitiesAdapter
    private lateinit var binding:FragmentHomeBinding

    private var user:User?=null
    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository,
            activity?.application as RunApplication
        )
    }

    private var resultLauncher =registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val goalValue = data?.getIntExtra(Constants.INTENT_SET_MYGOAL, 1000)
            goalValue?.let {
                val user = viewModel.getUserFromSharedPref()
                user?.setdistanceGoal(it.toLong())
                viewModel.updateUser(user!!)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initUserInfo()
        subscribeToObservers()
        observeError()
        getListToTraining()
        getListRecentExercise()
        binding.imageViewSetMyGoal.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                clickGoToSetMyGoal()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
        binding.textViewMoreTodayTraining.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                goToListExercisePage("Running for today training", 1)
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }
        binding.textViewMoreRecentTraining.setOnClickListener {
            if(CheckConnection.haveNetworkConnection(requireContext())){
                goToListRecentTraining()
            }else{
                Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
            }
        }

        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        }
    }

    private fun goToListRecentTraining() {
        val bundle = Bundle()
        bundle.putLong(Constants.ID_USER_RECENT_EXERCISE, user?.getId()!!)
        findNavController().navigate(R.id.action_homeFragment_to_listRecentExerciseFragment, bundle)
    }

    private fun goToListExercisePage(titleName:String, typeExercise:Int){
        val bundle = Bundle()
        bundle.putString(Constants.TITLE_NAME, titleName)
        bundle.putInt(Constants.TYPE_EXERCISE, typeExercise)
        findNavController().navigate(R.id.action_homeFragment_to_listExerciseFragment, bundle)
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
        binding.progressBar.max = ((user?.getdistanceGoal()?.times(1000f))?.toInt()?.div(100)) ?: 0
        val nameUser = "Let's go "
        val text = user?.getFullname()
        val text2 = nameUser+text
        val spannable: Spannable = SpannableString(text2)

        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.grey_200)), nameUser.length, (text + nameUser).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textViewWellcome.setText(spannable, TextView.BufferType.SPANNABLE)
    }

    private fun getListToTraining(){
        viewModel.getListActivityRun()
        viewModel.listActivityRun.observe(viewLifecycleOwner, Observer {
            initActivityList(it)
        })
    }

    private fun getListRecentExercise(){
        viewModel.getListUserExercise(user?.getId()!!)
        viewModel.recentExercise.observe(viewLifecycleOwner, Observer {
            initRecentActivities(it)
        })
    }

    private fun observeError(){
        viewModel.errEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun subscribeToObservers(){
        viewModel.totalDistanceWeekly.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewNumberDistance.text = (it/ 1000f).toString()
                binding.progressBar.progress = (it/100)
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
        Toast.makeText(requireContext(), binding.progressBar.max.toString(), Toast.LENGTH_SHORT).show()
        Toast.makeText(requireContext(), binding.progressBar.progress.toString(), Toast.LENGTH_SHORT).show()
        val intent = Intent(context, SetMyGoalActivity::class.java)
        val bundle = Bundle()
        bundle.putLong(Constants.INIT_SET_MYGOAL, user?.getdistanceGoal()!!)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    private fun goToActivityDetailPage(id:Long){
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        findNavController().navigate(R.id.action_homeFragment_to_detailExerciseFragment, bundle)
    }

    private fun initRecentActivities(list : List<UserActivityDetail>) {
        adapterRecentActivities = RecyclerViewRecentActivitiesAdapter(false)
        adapterRecentActivities.submitList(list)
        binding.recyclerViewRecentActiviesHomeFragment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRecentActiviesHomeFragment.adapter = adapterRecentActivities
        binding.recyclerViewRecentActiviesHomeFragment.setHasFixedSize(true)
        binding.recyclerViewRecentActiviesHomeFragment.isNestedScrollingEnabled = false
    }

    private fun initActivityList(list: List<vu.pham.runningappseminar.models.Activity>) {
        adapterTodayTraining = RecyclerViewActivityAdapter(R.layout.activity_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: vu.pham.runningappseminar.models.Activity) {
                if(CheckConnection.haveNetworkConnection(requireContext())){
                    goToActivityDetailPage(activity.getId())
                }else{
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        }, false, true, true)
        adapterTodayTraining.submitList(list)
        binding.recyclerViewActivityHomeFragment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        binding.recyclerViewActivityHomeFragment.adapter = adapterTodayTraining
        binding.recyclerViewActivityHomeFragment.setHasFixedSize(true)
    }
}