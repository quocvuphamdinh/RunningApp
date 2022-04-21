package vu.pham.runningappseminar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityMainBinding
import vu.pham.runningappseminar.utils.Constants

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    private lateinit var navHostFragment:NavHostFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.activity_main)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.bottomNavHome.background = null

        navHostFragment = supportFragmentManager
            .findFragmentById(R.id.navHostFragment) as NavHostFragment
        val navController = navHostFragment.navController
        binding.bottomNavHome.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
                when(destination.id){
                    R.id.homeFragment, R.id.activityFragment, R.id.analysisFragment, R.id.profileFragment -> {
                        binding.bottomNavHome.visibility = View.VISIBLE
                        binding.bottomAppBar.visibility = View.VISIBLE
                        binding.floatingButtonRun.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.bottomNavHome.visibility = View.GONE
                        binding.bottomAppBar.visibility = View.GONE
                        binding.floatingButtonRun.visibility = View.GONE
                    }
                }
            }
        binding.floatingButtonRun.setOnClickListener {
            clickRun()
        }
    }

    private fun goToExerciseRunFragmentIfNeeded(intent: Intent?){
        if(intent?.action == Constants.ACTION_SHOW_EXERCISE_RUN_FRAGMENT){
            navHostFragment.findNavController().navigate(R.id.action_global_exerciseRunFragment)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        goToRunActivityIfNeeded(intent)
        goToExerciseRunFragmentIfNeeded(intent)
    }
    private fun goToRunActivityIfNeeded(intent: Intent?){
        if(intent?.action== Constants.ACTION_SHOW_TRACKING_ACTIVITY){
            val intent2 = Intent(this@MainActivity, RunActivity::class.java)
            startActivity(intent2)
        }
    }
    private fun clickRun() {
        binding.floatingButtonRun.setOnClickListener {
            val intent = Intent(this@MainActivity, RunActivity::class.java)
            startActivity(intent)
        }
    }
}