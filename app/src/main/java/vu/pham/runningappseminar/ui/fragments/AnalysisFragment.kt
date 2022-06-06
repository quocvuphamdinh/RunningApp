package vu.pham.runningappseminar.ui.fragments

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import vu.pham.runningappseminar.databinding.FragmentAnalysisBinding
import vu.pham.runningappseminar.ui.adapters.ViewPagerGeneralAdapter

class AnalysisFragment : Fragment() {
    private lateinit var binding: FragmentAnalysisBinding
    private lateinit var viewPagerGeneralAdapter: ViewPagerGeneralAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViewPagerWithTabLayout()
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {}
    }

    private fun setUpViewPagerWithTabLayout() {
        viewPagerGeneralAdapter = ViewPagerGeneralAdapter(
            requireActivity(), listOf(
                AnalysisDayFragment(),
                AnalysisMonthFragment()
            )
        )
        binding.viewPagerAnalysis.adapter = viewPagerGeneralAdapter
        TabLayoutMediator(binding.tabLayoutAnalysis, binding.viewPagerAnalysis) { tab, position ->
            when (position) {
                0 -> tab.text = "Day"
                1 -> tab.text = "Month"
            }
        }.attach()
    }
}