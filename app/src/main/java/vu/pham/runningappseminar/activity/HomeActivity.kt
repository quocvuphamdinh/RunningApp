package vu.pham.runningappseminar.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.bottomappbar.BottomAppBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import vu.pham.runningappseminar.R
import vu.pham.runningappseminar.fragment.ActivityFragment
import vu.pham.runningappseminar.fragment.AnalysisFragment
import vu.pham.runningappseminar.fragment.HomeFragment
import vu.pham.runningappseminar.fragment.ProfileFragment
import vu.pham.runningappseminar.utils.Constants


class HomeActivity : AppCompatActivity() {
    private lateinit var bottomNav:BottomNavigationView
    private lateinit var bottomAppBar:BottomAppBar
    private lateinit var fab:FloatingActionButton
    private val HOME_FRAGMENT=1
    private val ACTIVITY_FRAGMENT=2
    private val ANALYSIS_FRAGMENT=3
    private val PROFILE_FRAGMENT=4
    private var currentFragment=HOME_FRAGMENT
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        anhXa()
        goToRunActivityIfNeeded(intent)
        clickItemInBottomAppBar()
        clickRun()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        goToRunActivityIfNeeded(intent)
    }
    private fun goToRunActivityIfNeeded(intent: Intent?){
        if(intent?.action== Constants.ACTION_SHOW_TRACKING_ACTIVITY){
            val intent2 = Intent(this@HomeActivity, RunActivity::class.java)
            startActivity(intent2)
        }
    }
    private fun clickRun() {
        fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, RunActivity::class.java)
            startActivity(intent)
        }
    }

    private fun clickItemInBottomAppBar(){
        bottomNav.setOnNavigationItemSelectedListener(object :NavigationView.OnNavigationItemSelectedListener,
            BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val id = item.itemId
                if(id==R.id.nav_home){
                    if(currentFragment!=HOME_FRAGMENT){
                        replaceFragment(HomeFragment())
                        currentFragment = HOME_FRAGMENT
                    }
                }else if(id==R.id.nav_activity){
                    if(currentFragment!=ACTIVITY_FRAGMENT){
                        replaceFragment(ActivityFragment())
                        currentFragment = ACTIVITY_FRAGMENT
                    }
                }else if(id==R.id.nav_analysis){
                    if(currentFragment!=ANALYSIS_FRAGMENT){
                        replaceFragment(AnalysisFragment())
                        currentFragment = ANALYSIS_FRAGMENT
                    }
                }else if(id == R.id.nav_profile){
                    if(currentFragment!=PROFILE_FRAGMENT){
                        replaceFragment(ProfileFragment())
                        currentFragment = PROFILE_FRAGMENT
                    }
                }
                return true
            }
        })
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction =  supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content_frame_layout, fragment)
        transaction.commit()
    }
    private fun anhXa() {
        fab = findViewById(R.id.floatingButtonRun)
        bottomNav = findViewById(R.id.bottomNavHome)
        bottomAppBar = findViewById(R.id.bottomAppBar)
        bottomNav.background = null
        bottomNav.menu.findItem(R.id.nav_home).isChecked = true
        replaceFragment(HomeFragment())
    }
}