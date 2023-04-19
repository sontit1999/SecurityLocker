package com.ls.entertainment.securitylocker.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ls.entertainment.securitylocker.ui.app.AppFragment
import com.ls.entertainment.securitylocker.ui.setting.SettingFragment
import com.ls.entertainment.securitylocker.ui.theme.ThemeFragment
import com.ls.entertainment.securitylocker.ui.tool.ToolFragment

class MainViewPagerAdapter(activity: FragmentActivity) : FragmentStateAdapter(activity) {
	
	override fun getItemCount() = 4
	
	override fun createFragment(position: Int): Fragment {
		val fragment = when (position) {
			0 -> AppFragment()
			1 -> ToolFragment()
			2 -> ThemeFragment()
			3 -> SettingFragment()
			else -> AppFragment()
		}
		return fragment
	}
}
