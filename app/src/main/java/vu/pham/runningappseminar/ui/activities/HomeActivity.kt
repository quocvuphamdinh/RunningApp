package vu.pham.runningappseminar.ui.activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityHomeBinding
import vu.pham.runningappseminar.ui.utils.setupWithNavController
import vu.pham.runningappseminar.utils.Constants

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var currentNavController: LiveData<NavController>? = null

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.Theme_RunningAppSeminar)
        //setContentView(R.layout.activity_home)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home)
        binding.bottomNavHome.background = null
        setupBottomNavigationBar()

        binding.floatingButtonRun.setOnClickListener {
            clickRun()
        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(R.navigation.nav_home, R.navigation.nav_exercise, R.navigation.nav_analysis, R.navigation.nav_profile)

        val controller = binding.bottomNavHome.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.navHostFragmentHome,
            intent
        )

        controller.observe(this, Observer { navController ->
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
        })
        currentNavController = controller
    }

    override fun onSupportNavigateUp(): Boolean {
        return currentNavController?.value?.navigateUp() ?: false
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        goToRunActivityIfNeeded(intent)
        goToExerciseRunFragmentIfNeeded(intent)
    }

    private fun goToExerciseRunFragmentIfNeeded(intent: Intent?){
        if(intent?.action == Constants.ACTION_SHOW_EXERCISE_RUN_FRAGMENT){
            currentNavController?.value?.navigate(R.id.action_global_exerciseRunFragment)
        }
    }

    private fun goToRunActivityIfNeeded(intent: Intent?){
        if(intent?.action== Constants.ACTION_SHOW_TRACKING_ACTIVITY){
            val intent2 = Intent(this@HomeActivity, RunActivity::class.java)
            startActivity(intent2)
        }
    }

    private fun clickRun() {
        binding.floatingButtonRun.setOnClickListener {
            val intent = Intent(this@HomeActivity, RunActivity::class.java)
            startActivity(intent)
        }
    }
}