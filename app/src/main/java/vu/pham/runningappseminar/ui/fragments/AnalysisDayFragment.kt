package vu.pham.runningappseminar.ui.fragments

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.FragmentAnalysisDayBinding
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.AnalysisViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.AnalysisViewModelFactory
import java.text.SimpleDateFormat
import androidx.lifecycle.Observer
import vu.pham.runningappseminar.ui.utils.CustomMarkerView
import java.util.*

class AnalysisDayFragment : Fragment() {
    private lateinit var binding: FragmentAnalysisDayBinding
    private var date = Date(System.currentTimeMillis())
    private val viewModel: AnalysisViewModel by viewModels {
        AnalysisViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAnalysisDayBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpDataToBarEntries()
        binding.imageViewSelectDate.setOnClickListener {
            showDialogSelectDate()
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
        val dateDate2 = SimpleDateFormat("dd/MM/yyyy")
        viewModel.getTotalDistanceInEachDay(date.time).observe(viewLifecycleOwner, Observer {
            if (it.all { item -> item == 0 }) {
                binding.textViewNoData1.visibility = View.VISIBLE
            } else {
                binding.textViewNoData1.visibility = View.GONE
            }
            val allDistance = it.indices.map { i -> BarEntry(i.toFloat(), it[i].toFloat()) }
            initBarChart(
                it,
                "Total Distance",
                "meters",
                binding.barChar1,
                resources.getColor(R.color.startColor),
                resources.getColor(
                    R.color.endColor
                ),
                resources.getStringArray(R.array.analysisLabel)[0],
                allDistance,
                dateDate2.format(date)
            )
        })
        viewModel.getTotalDurationInEachDay(date.time).observe(viewLifecycleOwner, Observer {
            if (it.all { item -> item == 0L }) {
                binding.textViewNoData2.visibility = View.VISIBLE
            } else {
                binding.textViewNoData2.visibility = View.GONE
            }
            val allDuration = it.indices.map { i -> BarEntry(i.toFloat(), it[i].toFloat()) }
            initBarChart(
                it,
                "Total Duration",
                "ms",
                binding.barChar2,
                resources.getColor(R.color.startColor2),
                resources.getColor(
                    R.color.endColor2
                ),
                resources.getStringArray(R.array.analysisLabel)[1],
                allDuration,
                dateDate2.format(date)
            )
        })
        viewModel.getTotalCaloriesBurnedInEachDay(date.time).observe(viewLifecycleOwner, Observer {
            if (it.all { item -> item == 0 }) {
                binding.textViewNoData3.visibility = View.VISIBLE
            } else {
                binding.textViewNoData3.visibility = View.GONE
            }
            val allCaloriesBurned = it.indices.map { i -> BarEntry(i.toFloat(), it[i].toFloat()) }
            initBarChart(
                it,
                "Total Calories Burned",
                "kcal",
                binding.barChar3,
                resources.getColor(R.color.calories_color),
                resources.getColor(
                    R.color.yellow
                ),
                resources.getStringArray(R.array.analysisLabel)[2],
                allCaloriesBurned,
                dateDate2.format(date)
            )
        })
        viewModel.getTotalAvgSpeedInEachDay(date.time).observe(viewLifecycleOwner, Observer {
            if (it.all { item -> item == 0F }) {
                binding.textViewNoData4.visibility = View.VISIBLE
            } else {
                binding.textViewNoData4.visibility = View.GONE
            }
            val allAvgSpeed = it.indices.map { i -> BarEntry(i.toFloat(), it[i]) }
            initBarChart(
                it,
                "Total Average Speed",
                "km/h",
                binding.barChar4,
                resources.getColor(R.color.avg_speed_color),
                resources.getColor(R.color.teal_200),
                resources.getStringArray(R.array.analysisLabel)[3],
                allAvgSpeed,
                dateDate2.format(date)
            )
        })
    }

    private fun initBarChart(
        value: List<Any>,
        title: String,
        type: String,
        barChart: BarChart,
        startColor: Int,
        endColor: Int,
        label: String,
        barEntry: List<BarEntry>,
        label2: String
    ) {
        val barDataSet = BarDataSet(barEntry, label)
        barDataSet.setColors(endColor)
        barDataSet.formLineWidth = 10f
        barDataSet.setGradientColor(startColor, endColor)
        barChart.data = BarData(barDataSet)
        val description = Description()
        description.text = label2
        description.textSize = 14F
        barChart.description = description
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(resources.getStringArray(R.array.day_array))
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        barChart.animateY(2000)
        barChart.marker =
            CustomMarkerView(value, title, type, requireContext(), R.layout.marker_view)
        barChart.invalidate()
    }
}