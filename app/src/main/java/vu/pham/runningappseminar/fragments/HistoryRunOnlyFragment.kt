package vu.pham.runningappseminar.fragments

import vu.pham.runningappseminar.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import vu.pham.runningappseminar.adapters.RecyclerViewHistoryRunAdapter
import vu.pham.runningappseminar.databinding.FragmentHistoryRunOnlyBinding
import vu.pham.runningappseminar.models.Run
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.SortType
import vu.pham.runningappseminar.viewmodels.HistoryRunOnlyViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.HistoryRunOnlyViewModelFactory

class HistoryRunOnlyFragment : Fragment() {
    private lateinit var binding : FragmentHistoryRunOnlyBinding
    private lateinit var runAdapter: RecyclerViewHistoryRunAdapter
    private val viewModel : HistoryRunOnlyViewModel by viewModels{
        HistoryRunOnlyViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryRunOnlyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
        setUpSpinner()

        when(viewModel.sortType){
            SortType.DATE -> binding.spinnerFilterRun.setSelection(0)
            SortType.RUNNING_TIME -> binding.spinnerFilterRun.setSelection(1)
            SortType.CALORIES_BURNED -> binding.spinnerFilterRun.setSelection(2)
            SortType.DISTANCE -> binding.spinnerFilterRun.setSelection(3)
            SortType.AVG_SPEED -> binding.spinnerFilterRun.setSelection(4)
        }

        viewModel.runs.observe(viewLifecycleOwner, Observer {
            runAdapter.submitList(it)
        })
    }

    private fun setUpSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, resources.getStringArray(
            vu.pham.runningappseminar.R.array.spinner_sort_type))
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
        runAdapter = RecyclerViewHistoryRunAdapter(object : RecyclerViewHistoryRunAdapter.ClickHistoryRun{
            override fun clickRunItem(run: Run) {
                val bundle = Bundle()
                bundle.putString(Constants.ID_RUN_DETAIL, run.id)
                findNavController().navigate(R.id.action_historyRunFragment_to_runDetailFragment, bundle)
            }
        })
        binding.recyclerViewHistoryRun.adapter = runAdapter
        binding.recyclerViewHistoryRun.layoutManager = LinearLayoutManager(requireContext())
    }
}