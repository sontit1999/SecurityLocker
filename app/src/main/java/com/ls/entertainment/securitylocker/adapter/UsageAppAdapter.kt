package com.ls.entertainment.securitylocker.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ls.entertainment.securitylocker.databinding.ItemUsageAppBinding
import com.ls.entertainment.securitylocker.model.UsageTimeAppModel
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener

class UsageAppAdapter : RecyclerView.Adapter<UsageAppAdapter.UsageAppViewHolder>() {

	var listUsageApp = mutableListOf<UsageTimeAppModel>()

	var onClickItem: ((UsageTimeAppModel) -> Unit)? = null

	@SuppressLint("NotifyDataSetChanged")
	fun setData(listUsageApp: MutableList<UsageTimeAppModel>) {
		this.listUsageApp.clear()
		this.listUsageApp.addAll(listUsageApp)
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsageAppViewHolder {
		return UsageAppViewHolder(
			ItemUsageAppBinding.inflate(
				LayoutInflater.from(parent.context), parent, false
			)
		)
	}

	override fun onBindViewHolder(holder: UsageAppViewHolder, position: Int) {
		val item = listUsageApp[position]
		holder.binData(item)
		holder.binding.btnUnInstall.setOnSafeClickListener {
			onClickItem?.invoke(item)
		}
	}

	override fun getItemCount() = listUsageApp.size

	class UsageAppViewHolder(val binding: ItemUsageAppBinding) :
		RecyclerView.ViewHolder(binding.root) {

		fun binData(usageTimeAppModel: UsageTimeAppModel) {
			binding.ivIcon.setImageDrawable(usageTimeAppModel.logo)
			binding.tvAppName.text = usageTimeAppModel.appName
			binding.tvTime.text = usageTimeAppModel.getTimeUsage()
		}
	}
}