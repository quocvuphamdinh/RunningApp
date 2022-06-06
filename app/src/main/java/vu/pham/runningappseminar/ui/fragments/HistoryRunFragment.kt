package vu.pham.runningappseminar.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import vu.pham.runningappseminar.ui.adapters.ViewPagerGeneralAdapter
import vu.pham.runningappseminar.databinding.FragmentHistoryRunBinding
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.HistoryRunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.HistoryRunViewModelFactory

class HistoryRunFragment : Fragment() {
    private lateinit var binding : FragmentHistoryRunBinding
    private lateinit var viewPagerGeneralAdapter: ViewPagerGeneralAdapter
    private val viewModel : HistoryRunViewModel by viewModels{
        HistoryRunViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHistoryRunBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViewPagerWithTabLayout()

        binding.textViewBackHistoryRun.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun setUpViewPagerWithTabLayout() {
        viewPagerGeneralAdapter = ViewPagerGeneralAdapter(
            requireActivity(), listOf(
                HistoryRunOnlyFragment(),
                HistoryRunWithExerciseFragment()
            )
        )
        binding.viewPagerHistoryRun.adapter = viewPagerGeneralAdapter
        TabLayoutMediator(binding.tabLayoutHistoryRun, binding.viewPagerHistoryRun) { tab, position ->
            when (position) {
                0 -> tab.text = "Run Only"
                1 -> tab.text = "Run With Exercise"
            }
        }.attach()
    }
}