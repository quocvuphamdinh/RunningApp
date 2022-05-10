package vu.pham.runningappseminar.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.tabs.TabLayoutMediator
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.adapters.ViewPagerHistoryRunAdapter
import vu.pham.runningappseminar.databinding.FragmentHistoryRunBinding
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.HistoryRunViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.HistoryRunViewModelFactory

class HistoryRunFragment : Fragment() {
    private lateinit var binding : FragmentHistoryRunBinding
    private lateinit var viewPagerHistoryRunAdapter: ViewPagerHistoryRunAdapter
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
            findNavController().navigate(R.id.action_historyRunFragment_to_profileFragment)
        }
    }
    private fun setUpViewPagerWithTabLayout() {
        viewPagerHistoryRunAdapter = ViewPagerHistoryRunAdapter(
            requireActivity(), listOf(
                HistoryRunOnlyFragment(),
                HistoryRunWithExerciseFragment()
            )
        )
        binding.viewPagerHistoryRun.adapter = viewPagerHistoryRunAdapter
        TabLayoutMediator(binding.tabLayoutHistoryRun, binding.viewPagerHistoryRun) { tab, position ->
            when (position) {
                0 -> tab.text = "Run Only"
                1 -> tab.text = "Run With Exercise"
            }
        }.attach()
    }
}