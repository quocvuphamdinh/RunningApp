package vu.pham.runningappseminar.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import com.google.android.material.snackbar.Snackbar
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.databinding.ActivityHomeBinding
import vu.pham.runningappseminar.ui.utils.setupWithNavController
import vu.pham.runningappseminar.utils.CheckConnection
import vu.pham.runningappseminar.utils.Constants
import vu.pham.runningappseminar.utils.RunApplication
import vu.pham.runningappseminar.viewmodels.HomeActivityViewModel
import vu.pham.runningappseminar.viewmodels.viewmodelfactories.HomeActivityViewModelFactory


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private var currentNavController: LiveData<NavController>? = null
    private lateinit var boardcastReceiver: BroadcastReceiver
    private lateinit var snackbar: Snackbar
    private lateinit var snackBarView: View
    private val viewModel: HomeActivityViewModel by viewModels {
        HomeActivityViewModelFactory((application as RunApplication).repository)
    }

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

        setUpSnackBar()
        boardcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (ConnectivityManager.CONNECTIVITY_ACTION == intent?.action) {
                    if (CheckConnection.haveNetworkConnection(context!!)) {
                        if (viewModel.isDisconnectedFirstTime) {
                            showSnackBarOn()
                            viewModel.syncDataRunToServer()
                        }
                    } else {
                        showSnackBarOff()
                        viewModel.isDisconnectedFirstTime = true
                    }
                }
            }
        }

        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(boardcastReceiver, intentFilter)
    }

    private fun setUpSnackBar() {
        snackbar = Snackbar.make(binding.navHostFragmentHome, "", Snackbar.LENGTH_LONG)
        snackbar.anchorView = binding.floatingButtonRun
        snackBarView = snackbar.view
        val params: CoordinatorLayout.LayoutParams =
            snackBarView.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.TOP
        snackBarView.layoutParams = params
    }

    private fun showSnackBarOn() {
        snackbar.setText("Your internet is on !!")
        val txtSnackBar =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        txtSnackBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_wifi, 0)
        snackbar.show()
    }

    private fun showSnackBarOff() {
        snackbar.setText("Your internet is off !!")
        val txtSnackBar =
            snackBarView.findViewById<TextView>(com.google.android.material.R.id.snackbar_text)
        txtSnackBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_no_wifi, 0)
        snackbar.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(boardcastReceiver)
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setupBottomNavigationBar() {
        val navGraphIds = listOf(
            R.navigation.nav_home,
            R.navigation.nav_exercise,
            R.navigation.nav_analysis,
            R.navigation.nav_profile
        )

        val controller = binding.bottomNavHome.setupWithNavController(
            navGraphIds,
            supportFragmentManager,
            R.id.navHostFragmentHome,
            intent
        )

        controller.observe(this, Observer { navController ->
            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
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
        goToRunActivityOrExerciseRunFragmentIfNeeded(intent)
    }

    private fun goToRunActivityOrExerciseRunFragmentIfNeeded(intent: Intent?) {
        if (intent?.action == Constants.ACTION_SHOW_TRACKING_ACTIVITY) {
            val intent2 = Intent(this@HomeActivity, RunActivity::class.java)
            startActivity(intent2)
        }else if (intent?.action == Constants.ACTION_SHOW_EXERCISE_RUN_FRAGMENT) {
            currentNavController?.value?.navigate(R.id.action_global_exerciseRunFragment)
        }
    }

    private fun clickRun() {
        binding.floatingButtonRun.setOnClickListener {
            val intent = Intent(this@HomeActivity, RunActivity::class.java)
            startActivity(intent)
        }
    }
}