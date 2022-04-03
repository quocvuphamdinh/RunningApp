package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapter.RecyclerViewHistoryRunAdapter
import vu.pham.runningappseminar.databinding.ActivityHistoryRunBinding
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.SortType
import vu.pham.runningappseminar.viewmodels.HistoryRunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.HistoryRunViewModelFactory

class HistoryRunActivity : AppCompatActivity() {
//    private lateinit var recyclerViewHistoryRun:RecyclerView
//    private lateinit var txtGoBack:TextView
//    private lateinit var spinnerFilter:Spinner
    private lateinit var binding:ActivityHistoryRunBinding
    private lateinit var runAdapter:RecyclerViewHistoryRunAdapter

    private val viewModel : HistoryRunViewModel by viewModels{
        HistoryRunViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_history_run)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history_run)

        //anhXa()
        setUpRecyclerView()
        setUpSpinner()

        when(viewModel.sortType){
            SortType.DATE -> binding.spinnerFilterRun.setSelection(0)
            SortType.RUNNING_TIME -> binding.spinnerFilterRun.setSelection(1)
            SortType.CALORIES_BURNED -> binding.spinnerFilterRun.setSelection(2)
            SortType.DISTANCE -> binding.spinnerFilterRun.setSelection(3)
            SortType.AVG_SPEED -> binding.spinnerFilterRun.setSelection(4)
        }
        binding.textViewBackHistoryRun.setOnClickListener {
            finish()
        }

        viewModel.runs.observe(this, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setUpSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(this@HistoryRunActivity, android.R.layout.simple_list_item_1, resources.getStringArray(R.array.spinner_sort_type))
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFilterRun.adapter =spinnerAdapter
        binding.spinnerFilterRun.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                when(position){
                    0 -> viewModel.sortRuns(SortType.DATE)
                    1 -> viewModel.sortRuns(SortType.RUNNING_TIME)
                    2 -> viewModel.sortRuns(SortType.CALORIES_BURNED)
                    3 -> viewModel.sortRuns(SortType.DISTANCE)
                    4 -> viewModel.sortRuns(SortType.AVG_SPEED)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }


    private fun setUpRecyclerView() {
        runAdapter = RecyclerViewHistoryRunAdapter()
        binding.recyclerViewHistoryRun.adapter = runAdapter
        binding.recyclerViewHistoryRun.layoutManager = LinearLayoutManager(this@HistoryRunActivity)
        binding.recyclerViewHistoryRun.setHasFixedSize(true)
    }

}