package vu.pham.runningappseminar.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerHistoryRunAdapter(fragmentActivity: FragmentActivity, private val fragments:List<Fragment>) : FragmentStateAdapter(fragmentActivity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        when(position){
            0-> return fragments[0]
            1-> return fragments[1]
        }
        return fragments[0]
    }
}