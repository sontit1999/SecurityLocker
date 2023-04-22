package com.ls.entertainment.securitylocker.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ls.entertainment.securitylocker.ui.usage.UsageFragment

class UsageStateAdapter(fragmentActivity: FragmentActivity) :
	FragmentStateAdapter(fragmentActivity) {
	override fun getItemCount() = 2

	override fun createFragment(position: Int): Fragment {
		val fragment = when (position) {
			0    -> UsageFragment.newInstance(UsageFragment.TYPE_LAST_24_HOUR)
			1    -> UsageFragment.newInstance(UsageFragment.TYPE_LAST_10_DAY)
			else -> {
				UsageFragment.newInstance(UsageFragment.TYPE_LAST_24_HOUR)
			}
		}
		return fragment
	}
}