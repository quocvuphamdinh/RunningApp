package vu.pham.runningappseminar.ui.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import vu.pham.runningappseminar.R

class CustomMarkerView(
    private val value: List<Any>,
    private val title: String,
    private val type: String,
    context: Context,
    layout: Int
) : MarkerView(context, layout) {

    @SuppressLint("SetTextI18n")
    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        super.refreshContent(e, highlight)

        if (e == null) {
            return
        }
        val txtTitle = findViewById<TextView>(R.id.textViewTitleMarkerView)
        val txtValue = findViewById<TextView>(R.id.textViewValueMarkerView)
        val currentValue = e.x.toInt()
        val item = value[currentValue]
        txtTitle.text = title
        txtValue.text = "$item $type"
    }

    override fun getOffset(): MPPointF {
        return MPPointF(-width/2f, -height.toFloat())
    }
}