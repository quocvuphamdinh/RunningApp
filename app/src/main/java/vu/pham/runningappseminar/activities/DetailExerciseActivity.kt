package vu.pham.runningappseminar.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapters.RecyclerViewWorkoutAdapter
import vu.pham.runningappseminar.databinding.ActivityDetailExerciseBinding
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.models.Workout
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.DetailExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.DetailExerciseViewModelFactory

class DetailExerciseActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailExerciseBinding
    private lateinit var workoutAdapter:RecyclerViewWorkoutAdapter
    private var id:Long?=null

    private val viewModel : DetailExerciseViewModel by viewModels{
        DetailExerciseViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_detail_exercise)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail_exercise)

        getIdFromPreviousPage()
        getActivityDetailFromRemote()

        binding.imageCloseDetailExcerciseActivity.setOnClickListener {
            finish()
        }
    }


    private fun getIdFromPreviousPage(){
        val bundle = intent?.extras
        id = bundle?.getLong(Constants.DETAIL_EXERCISE_ID)
    }
    private fun getActivityDetailFromRemote(){
        id?.let {
            viewModel.getActivityDetail(id!!)
            viewModel.activityDetail.observe(this, Observer {
                setUpRecyclerView(it.getWorkouts())
                bindDataToView(it)
            })
        }
    }

    private fun bindDataToView(activity: Activity){
        binding.textViewTitleDetailExercise.text = activity.getName()
        setUpRecyclerView(activity.getWorkouts())
    }
    private fun setUpRecyclerView(list: List<Workout>) {
        workoutAdapter = RecyclerViewWorkoutAdapter()
        workoutAdapter.submitList(list)
        binding.rcvDetailExercise.layoutManager = LinearLayoutManager(this@DetailExerciseActivity, LinearLayoutManager.VERTICAL, false)
        binding.rcvDetailExercise.adapter = workoutAdapter
        binding.rcvDetailExercise.setHasFixedSize(true)
    }
}