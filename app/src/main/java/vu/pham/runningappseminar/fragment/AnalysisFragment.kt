package vu.pham.runningappseminar.fragment

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.utils.TrackingUtil
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory
import java.text.SimpleDateFormat
import java.util.*

class AnalysisFragment : Fragment() {

    private lateinit var barChart1:BarChart
    private lateinit var barChart2:BarChart
    private lateinit var barChart3:BarChart
    private lateinit var imageViewSelectDate:ImageView
    private var date = Date(System.currentTimeMillis())

    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.analysis_fragment, container, false)

        anhXa(view)

        setUpDataToBarEntries()

        imageViewSelectDate.setOnClickListener {
            showDialogSelectDate()
        }

        return view
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showDialogSelectDate() {
       val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog)
        val datePicker = bottomSheetDialog.findViewById<DatePicker>(R.id.datePicker)
        datePicker?.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
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
        viewModel.getListDistanceInSpecificDate(dateDate.format(date)).observe(viewLifecycleOwner, Observer {
            val allDistance = it.indices.map { i-> BarEntry(i.toFloat(), it[i].distanceInKilometers.toFloat()) }
            val allDuration = it.indices.map { i-> BarEntry(i.toFloat(), it[i].timeInMillis.toFloat()) }
            val allCaloriesBurned = it.indices.map { i-> BarEntry(i.toFloat(), it[i].caloriesBurned.toFloat()) }
            initBarChart(barChart1, resources.getColor(R.color.startColor), resources.getColor(R.color.endColor),
                resources.getStringArray(R.array.analysisLabel)[0], allDistance, dateDate.format(date))
            initBarChart(barChart2, resources.getColor(R.color.startColor2), resources.getColor(R.color.endColor2),
                resources.getStringArray(R.array.analysisLabel)[1], allDuration, dateDate.format(date))
            initBarChart(barChart3, resources.getColor(R.color.startColor3), resources.getColor(R.color.endColor3),
                resources.getStringArray(R.array.analysisLabel)[2], allCaloriesBurned, dateDate.format(date))
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
        barChart.description = description
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        barChart.animateY(2000)
        barChart.invalidate()
    }

    private fun anhXa(view: View) {
        barChart1 = view.findViewById(R.id.barChar1)
        barChart2 = view.findViewById(R.id.barChar2)
        barChart3 = view.findViewById(R.id.barChar3)
        imageViewSelectDate = view.findViewById(R.id.imageViewSelectDate)
    }
}