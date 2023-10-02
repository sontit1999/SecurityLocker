package com.ls.entertainment.securitylocker.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

import com.ls.entertainment.securitylocker.R
import com.ls.entertainment.securitylocker.databinding.ItemLanguageBinding
import com.ls.entertainment.securitylocker.model.Language
import com.ls.entertainment.securitylocker.utils.setOnSafeClickListener
import java.util.*

class LanguageAdapter(
	private val ctx: Context,

) :
	RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

	private var languages: MutableList<Language> = mutableListOf()
	private var posSelected = -1
	var onClickedItem: ((String) -> Unit)? = null

	fun setData(listLocation: MutableList<Language>) {
		this.languages = listLocation
		notifyDataSetChanged()
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
		val binding = ItemLanguageBinding.inflate(LayoutInflater.from(ctx), parent, false)
		return LanguageViewHolder(binding)
	}

	override fun getItemCount(): Int = languages.size

	override fun onBindViewHolder(holder: LanguageViewHolder, @SuppressLint("RecyclerView") position: Int) {
		holder.binding.imgLogoLocation.setImageResource(languages[position].img)
		holder.binding.tvLanguage.text = languages[position].country
		if (posSelected == position) {
			holder.binding.tvLanguage.setTextColor(
				ContextCompat.getColor(
					holder.binding.root.context,
					R.color.black
				)
			)
			holder.binding.layoutLanguage.setBackgroundResource(R.drawable.bg_choose_language)
		} else {
			holder.binding.tvLanguage.setTextColor(
				ContextCompat.getColor(
					holder.binding.root.context,
					R.color.black
				)
			)
			holder.binding.layoutLanguage.setBackgroundResource(R.drawable.bg_language_v2)
		}
		holder.binding.layoutLanguage.setOnSafeClickListener {
			posSelected = position
			onClickedItem?.invoke(languages[posSelected].lang)
			notifyDataSetChanged()
		}
	}

	class LanguageViewHolder(val binding: ItemLanguageBinding) :
		RecyclerView.ViewHolder(binding.root)
}