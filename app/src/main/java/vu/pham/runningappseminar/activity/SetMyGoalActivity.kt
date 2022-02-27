package vu.pham.runningappseminar.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.NumberPicker
import vu.pham.runningappseminar.R


class SetMyGoalActivity : AppCompatActivity() {
    private lateinit var numberPicker:NumberPicker
    private lateinit var imgClose:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_my_goal)

        anhXa()
        initNumberPicker()
        closeSetMyGoal()
    }

    private fun closeSetMyGoal() {
        imgClose.setOnClickListener {
            finish()
        }
    }

    private fun initNumberPicker(){
        numberPicker.maxValue = Int.MAX_VALUE
        numberPicker.minValue = 1
        numberPicker.value = 1
        numberPicker.wrapSelectorWheel = false
    }
    private fun anhXa() {
        numberPicker = findViewById(R.id.numberPicker1)
        imgClose = findViewById(R.id.imageCloseSetMyGoal)
    }
}