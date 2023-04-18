package com.ls.entertainment.securitylocker.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ls.entertainment.securitylocker.ui.AppFragment

class MainViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {

    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> AppFragment()
            1 -> AppFragment()
            2 -> AppFragment()
            3 -> AppFragment()
            else -> AppFragment()
        }
        return fragment
    }
}
