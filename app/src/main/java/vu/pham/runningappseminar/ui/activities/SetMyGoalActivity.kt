package vu.pham.runningappseminar.ui.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivitySetMyGoalBinding
import vu.pham.runningappseminar.utils.Constants


class SetMyGoalActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySetMyGoalBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_RunningAppSeminar)
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_set_my_goal)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_set_my_goal)

        initNumberPicker()
        binding.imageCloseSetMyGoal.setOnClickListener {
            closeSetMyGoal()
        }
        binding.buttonSetMyGoal.setOnClickListener {
            onCLickBackToHome()
        }
    }

    private fun onCLickBackToHome() {
        val intent = Intent()
        val goalValue = binding.numberPicker1.value
        intent.putExtra(Constants.INTENT_SET_MYGOAL, goalValue)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun closeSetMyGoal() {
        finish()
    }

    private fun initNumberPicker(){
        binding.numberPicker1.maxValue = Int.MAX_VALUE
        binding.numberPicker1.minValue = 1
        val bundle = intent?.extras
        val data = bundle?.getLong(Constants.INIT_SET_MYGOAL, 1)?.toInt()
        binding.numberPicker1.value = if(data==0) 1 else data!!
        binding.numberPicker1.wrapSelectorWheel = false
    }
}