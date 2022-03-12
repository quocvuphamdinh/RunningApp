package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapter.RecyclerViewHistoryRunAdapter
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory

class HistoryRunActivity : AppCompatActivity() {
    private lateinit var recyclerViewHistoryRun:RecyclerView
    private lateinit var txtGoBack:TextView
    private lateinit var runAdapter:RecyclerViewHistoryRunAdapter

    private val viewModel :MainViewModel by viewModels{
        MainViewModelFactory((application as RunApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_run)

        anhXa()
        setUpRecyclerView()

        txtGoBack.setOnClickListener {
            finish()
        }

        viewModel.runSortedByDate.observe(this, Observer {
            runAdapter.submitList(it)
        })
    }


    private fun setUpRecyclerView() {
        runAdapter = RecyclerViewHistoryRunAdapter()
        recyclerViewHistoryRun.adapter = runAdapter
        recyclerViewHistoryRun.layoutManager = LinearLayoutManager(this@HistoryRunActivity)
        recyclerViewHistoryRun.setHasFixedSize(true)
    }

    private fun anhXa() {
        recyclerViewHistoryRun = findViewById(R.id.recyclerViewHistoryRun)
        txtGoBack= findViewById(R.id.textViewBackHistoryRun)
    }
}