package vu.pham.runningappseminar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapters.RecyclerViewActivityAdapter
import vu.pham.runningappseminar.databinding.ActivityListExerciseBinding
import vu.pham.runningappseminar.models.Activity
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.ListExerciseViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.ListExerciseViewModelFactory

class ListExerciseActivity : AppCompatActivity() {
    private lateinit var binding:ActivityListExerciseBinding
    private lateinit var exerciseAdapter : RecyclerViewActivityAdapter
    private var titleName=""
    private var type=-1
    private val viewModel : ListExerciseViewModel by viewModels{
        ListExerciseViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_list_exercise)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_list_exercise)

        getData()
        getListExercise()
        registerObserver()

        binding.imageViewBackListExercise.setOnClickListener {
            finish()
        }
    }

    private fun getData() {
        val bundle = intent?.extras
        bundle?.let {
            titleName = it.getString(Constants.TITLE_NAME)!!
            type = it.getInt(Constants.TYPE_EXERCISE)
            binding.textViewTitle.text = titleName
        }
    }

    private fun getListExercise(){
        if(type==0){
            viewModel.getWalkingExercises()
        }else{
            viewModel.getRunningExercises()
        }
    }
    private fun registerObserver() {
        if(type==0){
            viewModel.walkingExercises.observe(this, Observer {
                setUpRecyclerViewRunning(it, false, R.layout.walking_item_row)
            })
        }else{
            viewModel.runningExercises.observe(this, Observer {
                setUpRecyclerViewRunning(it, true, R.layout.running_item_row)
            })
        }
    }

    private fun goToActivityDetailPage(id:Long){
        val intent = Intent(this@ListExerciseActivity, DetailExerciseActivity::class.java)
        val bundle = Bundle()
        bundle.putLong(Constants.DETAIL_EXERCISE_ID, id)
        intent.putExtras(bundle)
        startActivity(intent)
    }

    private fun setUpRecyclerViewRunning(list:List<Activity>, isRunning:Boolean, layout:Int) {
        exerciseAdapter = RecyclerViewActivityAdapter(layout, object : RecyclerViewActivityAdapter.ClickItem{
            override fun clickItem(activity: Activity) {
                goToActivityDetailPage(activity.getId())
            }
        }, true, isRunning, false)
        exerciseAdapter.submitList(list)
        binding.rcvExerciseWeightLoss.layoutManager = LinearLayoutManager(this@ListExerciseActivity, LinearLayoutManager.VERTICAL, false)
        binding.rcvExerciseWeightLoss.adapter = exerciseAdapter
        binding.rcvExerciseWeightLoss.setHasFixedSize(true)
    }
}