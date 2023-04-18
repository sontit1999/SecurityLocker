package com.ls.entertainment.securitylocker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ItemAppBinding
import com.ls.entertainment.securitylocker.model.AppModel

class AppAdapter : RecyclerView.Adapter<AppAdapter.AppHolder>() {

    private val listApp = mutableListOf<AppModel>()
    var onClickItem: ((AppModel) -> Unit)? = null

    fun setData(listApp: MutableList<AppModel>) {
        this.listApp.clear()
        this.listApp.addAll(listApp)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppHolder {
        return AppHolder(ItemAppBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = listApp.size

    override fun onBindViewHolder(holder: AppHolder, position: Int) {
        holder.bindData(listApp[position])
    }

    inner class AppHolder(val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.ivLock.setOnClickListener {
                listApp[adapterPosition].isLock = !listApp[adapterPosition].isLock
                notifyItemChanged(adapterPosition)
                onClickItem?.invoke(listApp[adapterPosition])
            }
        }

        fun bindData(appModel: AppModel) {
            binding.ivApp.setImageDrawable(appModel.resIcon)
            binding.tvAppName.text = appModel.name
            if (appModel.isLock) {
                binding.ivLock.setImageResource(R.drawable.baseline_lock_24)
            } else {
                binding.ivLock.setImageResource(R.drawable.baseline_lock_open_24)
            }
        }
    }
}