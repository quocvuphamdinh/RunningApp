package vu.pham.runningappseminar.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.MainViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.MainViewModelFactory

class AnalysisFragment : Fragment() {

    private lateinit var spinner1:Spinner
    private lateinit var spinner2: Spinner
    private lateinit var spinner3: Spinner
    private lateinit var barChart1:BarChart
    private lateinit var barChart2:BarChart
    private lateinit var barChart3:BarChart
    private lateinit var barEntriesDistance:ArrayList<BarEntry>
    private lateinit var barEntriesDuration:ArrayList<BarEntry>
    private lateinit var barEntriesCaloriesBurned:ArrayList<BarEntry>
    private lateinit var spinnerAdapter:ArrayAdapter<String>

    private val viewModel : MainViewModel by viewModels{
        MainViewModelFactory((activity?.application as RunApplication).repository)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.analysis_fragment, container, false)

        anhXa(view)

        initSpinner(spinner1, barChart1, 0, resources.getColor(R.color.startColor), resources.getColor(R.color.endColor), barEntriesDistance)
        initSpinner(spinner2, barChart2, 1, resources.getColor(R.color.startColor2), resources.getColor(R.color.endColor2), barEntriesDuration)
        initSpinner(spinner3, barChart3, 2, resources.getColor(R.color.startColor3), resources.getColor(R.color.endColor3), barEntriesCaloriesBurned)

        barEntriesDistance = ArrayList()
        barEntriesDistance.clear()
        getRunDayOfWeek(1, 0F)
        getRunDayOfWeek(2, 1F)
        getRunDayOfWeek(3, 2F)
        getRunDayOfWeek(4, 3F)
        getRunDayOfWeek(5, 4F)
        getRunDayOfWeek(6, 5F)
        getRunDayOfWeek(7, 6F)

        barEntriesDuration = ArrayList()
        barEntriesDuration.clear()

        barEntriesCaloriesBurned= ArrayList()
        barEntriesCaloriesBurned.clear()
        return view
    }

    override fun onResume() {
        super.onResume()
        barEntriesDistance.clear()
        getRunDayOfWeek(1, 0F)
        getRunDayOfWeek(2, 1F)
        getRunDayOfWeek(3, 2F)
        getRunDayOfWeek(4, 3F)
        getRunDayOfWeek(5, 4F)
        getRunDayOfWeek(6, 5F)
        getRunDayOfWeek(7, 6F)

        barEntriesDuration = ArrayList()
        barEntriesDuration.clear()

        barEntriesCaloriesBurned= ArrayList()
        barEntriesCaloriesBurned.clear()
    }


    private fun getRunDayOfWeek(date:Int, index:Float){
        lifecycleScope.launch {
           val data= viewModel.getTotalDitanceInSpecificDayOfWeek(date.toString())
            barEntriesDistance.add(BarEntry(index, (data?.toFloat()?:0f)/1000f))
        }
    }
    private fun initSpinner(spinner: Spinner, barChart: BarChart, index:Int, startColor:Int, endColor:Int, barEntry: List<BarEntry>) {
        spinnerAdapter = context?.let { ArrayAdapter<String>(it, android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.spinner_data)) }!!
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = spinnerAdapter
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position==0){
                    initBarChart(barChart, startColor, endColor, resources.getStringArray(R.array.spinner_data)[position],
                        resources.getStringArray(R.array.analysisLabel)[index],
                        resources.getStringArray(R.array.date_array), barEntry)
                }else if(position==1){
                    initBarChart(barChart, startColor, endColor, resources.getStringArray(R.array.spinner_data)[position],
                        resources.getStringArray(R.array.analysisLabel)[index],
                        resources.getStringArray(R.array.month_array), barEntry)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }


    private fun initBarChart(barChart:BarChart, startColor:Int, endColor:Int, label:String, label2:String, lists: Array<String>, barEntry: List<BarEntry>) {
//        for (i in 0 until lists.size){
//            barEntries.add(BarEntry(i.toFloat(),
//                viewModel.getTotalDitanceInSpecificDayOfWeek("weekday 0").toFloat()
//            ))
//        }

        val barDataSet = BarDataSet(barEntry, label2)
        barDataSet.setColors(endColor)
        barDataSet.formLineWidth= 10f
        barDataSet.setGradientColor(startColor, endColor)
        barChart.data = BarData(barDataSet)
        val description = Description()
        description.text = label
        barChart.description = description
        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(lists)
        xAxis.position = XAxis.XAxisPosition.TOP
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
        xAxis.granularity = 1f
        xAxis.labelCount = lists.size
        barChart.animateY(2000)
        barChart.invalidate()
    }

    private fun anhXa(view: View) {
        barChart1 = view.findViewById(R.id.barChar1)
        barChart2 = view.findViewById(R.id.barChar2)
        barChart3 = view.findViewById(R.id.barChar3)
        spinner1 = view.findViewById(R.id.spinner1)
        spinner2 = view.findViewById(R.id.spinner2)
        spinner3 = view.findViewById(R.id.spinner3)
    }
}