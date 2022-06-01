package vu.pham.runningappseminar.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.addCallback
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.google.android.material.bottomsheet.BottomSheetDialog
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.AnalysisViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.AnalysisViewModelFactory
import vu.pham.runningappseminar.databinding.FragmentAnalysisBinding
import java.text.SimpleDateFormat
import java.util.*

class AnalysisFragment : Fragment() {

    private lateinit var binding: FragmentAnalysisBinding
    private var date = Date(System.currentTimeMillis())

    private val viewModel : AnalysisViewModel by viewModels{
        AnalysisViewModelFactory((activity?.application as RunApplication).repository)
    }

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
        setUpDataToBarEntries()

        binding.imageViewSelectDate.setOnClickListener {
            showDialogSelectDate()
        }
        val callback = requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialogSelectDate() {
       val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val datePicker = bottomSheetDialog.findViewById<DatePicker>(R.id.datePicker)
        datePicker?.setOnDateChangedListener { _, year, monthOfYear, dayOfMonth ->
            date.year = year - 1900
            date.month = monthOfYear
            date.date = dayOfMonth
            setUpDataToBarEntries()
        }
        bottomSheetDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setUpDataToBarEntries() {
        val dateDate = SimpleDateFormat("yyyy-MM-dd")
        val dateDate2 = SimpleDateFormat("dd/MM/yyyy")
        viewModel.getListDistanceInSpecificDate(dateDate.format(date)).observe(viewLifecycleOwner, Observer {
            if(it.isNotEmpty()){
                binding.textViewNoData1.visibility = View.GONE
                binding.textViewNoData2.visibility = View.GONE
                binding.textViewNoData3.visibility = View.GONE
            }else{
                binding.textViewNoData1.visibility = View.VISIBLE
                binding.textViewNoData2.visibility = View.VISIBLE
                binding.textViewNoData3.visibility = View.VISIBLE
            }
            val allDistance = it.indices.map { i-> BarEntry(i.toFloat(), it[i].distanceInKilometers.toFloat()) }
            val allDuration = it.indices.map { i-> BarEntry(i.toFloat(), it[i].timeInMillis.toFloat()) }
            val allCaloriesBurned = it.indices.map { i-> BarEntry(i.toFloat(), it[i].caloriesBurned.toFloat()) }
            initBarChart(binding.barChar1, resources.getColor(R.color.startColor), resources.getColor(R.color.endColor),
                resources.getStringArray(R.array.analysisLabel)[0], allDistance, dateDate2.format(date))
            initBarChart(binding.barChar2, resources.getColor(R.color.startColor2), resources.getColor(R.color.endColor2),
                resources.getStringArray(R.array.analysisLabel)[1], allDuration, dateDate2.format(date))
            initBarChart(binding.barChar3, resources.getColor(R.color.startColor3), resources.getColor(R.color.endColor3),
                resources.getStringArray(R.array.analysisLabel)[2], allCaloriesBurned, dateDate2.format(date))
        })
    }


    private fun initBarChart(barChart:BarChart, startColor:Int, endColor:Int, label:String, barEntry: List<BarEntry>, label2:String) {
        val barDataSet = BarDataSet(barEntry, label)
        barDataSet.setColors(endColor)
        barDataSet.formLineWidth= 10f
        barDataSet.setGradientColor(startColor, endColor)
        barChart.data = BarData(barDataSet)
        val description = Description()
        description.text = label2
        description.textSize = 14F
        barChart.description = description
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        barChart.animateY(2000)
        barChart.invalidate()
    }
}