package com.ls.entertainment.securitylocker.adapter

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ItemAppBinding
import com.ls.entertainment.securitylocker.model.AppModel
import com.ls.entertainment.securitylocker.model.CheckPermissionEvent
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener
import org.greenrobot.eventbus.EventBus


const val DURATION_ANIMATION: Long = 150

class AppAdapter : RecyclerView.Adapter<AppAdapter.AppHolder>() {

    private val listApp = mutableListOf<AppModel>()
    var onClickLock: ((AppModel) -> Unit)? = null
    var onClickItem: ((AppModel) -> Unit)? = null
    var onAttach = true
    
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
        fromRightToLeft(holder.itemView, position)
    }
    
    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(@NonNull recyclerView: RecyclerView, newState: Int) {
                onAttach = false
                super.onScrollStateChanged(recyclerView, newState)
            }
        })
        
    }
    
    private fun setAnimation(itemView: View, i: Int) {
        var i = i
        if (!onAttach) {
            i = -1
        }
        val isNotFirstItem = i == -1
        i++
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animator = ObjectAnimator.ofFloat(itemView, "alpha", 0f, 0.5f, 1.0f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animator.startDelay =
            if (isNotFirstItem) DURATION_ANIMATION / 2 else i * DURATION_ANIMATION / 3
        animator.duration = 500
        animatorSet.play(animator)
        animator.start()
    }
    
    private fun fromLeftToRight(itemView: View, i: Int) {
        var i = i
        if (!onAttach) {
            i = -1
        }
        val notFirstItem = i == -1
        i += 1
        itemView.translationX = -400f
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animatorTranslateY = ObjectAnimator.ofFloat(itemView, "translationX", -400f, 0f)
        val animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animatorTranslateY.startDelay =
            if (notFirstItem) DURATION_ANIMATION else i * DURATION_ANIMATION
        animatorTranslateY.duration = (if (notFirstItem) 2 else 1) * DURATION_ANIMATION
        animatorSet.playTogether(animatorTranslateY, animatorAlpha)
        animatorSet.start()
    }
    
    private fun fromRightToLeft(itemView: View, i: Int) {
        var i = i
        if (!onAttach) {
            i = -1
        }
        val notFirstItem = i == -1
        i += 1
        itemView.translationX = itemView.x + 400
        itemView.alpha = 0f
        val animatorSet = AnimatorSet()
        val animatorTranslateY =
            ObjectAnimator.ofFloat(itemView, "translationX", itemView.x + 400, 0f)
        val animatorAlpha = ObjectAnimator.ofFloat(itemView, "alpha", 1f)
        ObjectAnimator.ofFloat(itemView, "alpha", 0f).start()
        animatorTranslateY.startDelay =
            if (notFirstItem) DURATION_ANIMATION else i * DURATION_ANIMATION
        animatorTranslateY.duration = (if (notFirstItem) 2 else 1) * DURATION_ANIMATION
        animatorSet.playTogether(animatorTranslateY, animatorAlpha)
        animatorSet.start()
    }
    
    inner class AppHolder(val binding: ItemAppBinding) :
        RecyclerView.ViewHolder(binding.root) {
        
        init {
            binding.ivLock.setOnClickListener {
                EventBus.getDefault().post(CheckPermissionEvent(true))
                listApp[adapterPosition].isLock = !listApp[adapterPosition].isLock
                if (listApp[adapterPosition].isLock) {
                    binding.ivLock.setImageResource(R.drawable.baseline_lock_24)
                } else {
                    binding.ivLock.setImageResource(R.drawable.baseline_lock_open_24)
                }
                onClickLock?.invoke(listApp[adapterPosition])
            }

            binding.ivInfo.setOnSafeClickListener {
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