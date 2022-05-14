package vu.pham.runningappseminar.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
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
import vu.pham.runningappseminar.models.User
import vu.pham.runningappseminar.models.UserActivityDetail
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.databinding.FragmentHomeBinding
import vu.pham.runningappseminar.viewmodels.HomePageViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.HomePageViewModelFactory


class HomeFragment : Fragment() {
    private lateinit var adapterTodayTraining:RecyclerViewActivityAdapter
    private lateinit var adapterRecentActivities:RecyclerViewRecentActivitiesAdapter
    private lateinit var binding:FragmentHomeBinding
    private var user:User?=null
    private val viewModel : HomePageViewModel by viewModels{
        HomePageViewModelFactory((activity?.application as RunApplication).repository,
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
                showCongratulationDistanceGoal(binding.progressBar.progress, ((user?.getdistanceGoal()?.times(1000f))?.toInt()?.div(100)) ?: 0)
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

        subscribeToObservers()
        initUserInfo()
        setUpRecyclerViewActivity()
        setUpRecyclerViewRecentActivities()
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

    private fun showCongratulationDistanceGoal(progress : Int, max : Int){
        if(progress==0 && max==0){
            binding.linearLayoutCongratulation.visibility = View.GONE
        }
        if(progress >= max && progress > 0){
            binding.linearLayoutCongratulation.visibility = View.VISIBLE
            binding.textViewCongratulation.text = "Congratulation you passed your distance goal !!"
        }else{
            binding.linearLayoutCongratulation.visibility = View.GONE
        }
    }

    private fun goToListRecentTraining() {
        val bundle = Bundle()
        bundle.putLong(Constants.ID_USER_RECENT_EXERCISE, user?.getId()!!)
        findNavController().navigate(R.id.action_homeFragment_to_listRecentExerciseFragment, bundle)
    }

    private fun goToListExercisePage(titleName:String, typeExercise:Int){
        viewModel.clearToast()
        val bundle = Bundle()
        bundle.putString(Constants.TITLE_NAME, titleName)
        bundle.putInt(Constants.TYPE_EXERCISE, typeExercise)
        findNavController().navigate(R.id.action_homeFragment_to_listExerciseFragment, bundle)
    }

    private fun initUserInfo() {
        user = viewModel.getUserFromSharedPref()
        viewModel.getUserLiveData(user!!.getUsername(), user!!.getPassword())
    }
    private fun bindDataToView(user: User?){
        binding.textViewWeeklyGoal.text = "Weekly goal ${user?.getdistanceGoal()} km"
        binding.progressBar.max = if(user?.getdistanceGoal()==0L) 0 else (user?.getdistanceGoal()?.times(1000f))?.toInt()?.div(100)!!
        val nameUser = "Let's go "
        val text = user.getFullname()
        val text2 = nameUser+text
        val spannable: Spannable = SpannableString(text2)

        spannable.setSpan(ForegroundColorSpan(resources.getColor(R.color.grey_200)), nameUser.length, (text + nameUser).length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.textViewWellcome.setText(spannable, TextView.BufferType.SPANNABLE)
        if(user.getdistanceGoal() ==0L){
            showCongratulationDistanceGoal(0, binding.progressBar.max)
        }
    }

    private fun getListToTraining(){
        viewModel.getListActivityRun(user?.getId()!!)
    }

    private fun getListRecentExercise(){
        viewModel.getListUserExercise(user?.getId()!!)
    }

    private fun subscribeToObservers(){
        viewModel.recentExercise.observe(viewLifecycleOwner, Observer {
            adapterRecentActivities.submitList(it)
        })

        viewModel.listActivityRun.observe(viewLifecycleOwner, Observer {
            adapterTodayTraining.submitList(it)
        })

        viewModel.userLiveData.observe(viewLifecycleOwner, Observer {
            user = it
            viewModel.getUserLiveData(user!!.getUsername(), user!!.getPassword())
            bindDataToView(user)
        })

        viewModel.toastEvent.observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.totalDistanceWeekly.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.textViewNumberDistance.text = (it/ 1000f).toString()
                binding.progressBar.progress = (it/100)
                showCongratulationDistanceGoal(it/100, ((user?.getdistanceGoal()?.times(1000f))?.toInt()?.div(100)) ?: 0)
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
        viewModel.clearToast()
        val intent = Intent(context, SetMyGoalActivity::class.java)
        val bundle = Bundle()
        bundle.putLong(Constants.INIT_SET_MYGOAL, user?.getdistanceGoal()!!)
        intent.putExtras(bundle)
        resultLauncher.launch(intent)
    }

    private fun goToActivityDetailPage(id:Long){
        viewModel.clearToast()
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        findNavController().navigate(R.id.action_homeFragment_to_detailExerciseFragment, bundle)
    }

    private fun setUpRecyclerViewRecentActivities() {
        adapterRecentActivities = RecyclerViewRecentActivitiesAdapter(false, object : RecyclerViewRecentActivitiesAdapter.ClickUserActivity{
            override fun clickItem(userActivityDetail: UserActivityDetail) {
                if (CheckConnection.haveNetworkConnection(requireContext())){
                    viewModel.clearToast()
                    val bundle = Bundle()
                    bundle.putLong(Constants.ID_RECENT_EXERCISE, userActivityDetail.getId())
                    findNavController().navigate(R.id.action_homeFragment_to_resultExerciseRunFragment, bundle)
                }else {
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        })
        binding.recyclerViewRecentActiviesHomeFragment.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.recyclerViewRecentActiviesHomeFragment.adapter = adapterRecentActivities
        binding.recyclerViewRecentActiviesHomeFragment.isNestedScrollingEnabled = false
    }

    private fun setUpRecyclerViewActivity() {
        adapterTodayTraining = RecyclerViewActivityAdapter(R.layout.activity_item_row, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: vu.pham.runningappseminar.models.Activity) {
                if(CheckConnection.haveNetworkConnection(requireContext())){
                    goToActivityDetailPage(activity.getId())
                }else{
                    Toast.makeText(context, "Your device does not have internet !", Toast.LENGTH_LONG).show()
                }
            }
        }, false, true, true)
        binding.recyclerViewActivityHomeFragment.adapter = adapterTodayTraining
        binding.recyclerViewActivityHomeFragment.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }
}