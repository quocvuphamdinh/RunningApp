package vu.pham.runningappseminar.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContract
import com.google.android.material.button.MaterialButton
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.utils.Constants


class SetMyGoalActivity : AppCompatActivity() {
    private lateinit var numberPicker:NumberPicker
    private lateinit var imgClose:ImageView
    private lateinit var btnSetMyGoal:MaterialButton
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_my_goal)

        anhXa()
        initNumberPicker()
        imgClose.setOnClickListener {
            closeSetMyGoal()
        }
        btnSetMyGoal.setOnClickListener {
            onCLickBackToHome()
        }
    }

    private fun onCLickBackToHome() {
        val intent = Intent()
        val goalValue =numberPicker.value
        intent.putExtra(Constants.INTENT_SET_MYGOAL, goalValue)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun closeSetMyGoal() {
        finish()
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
        btnSetMyGoal = findViewById(R.id.buttonSetMyGoal)
    }
}