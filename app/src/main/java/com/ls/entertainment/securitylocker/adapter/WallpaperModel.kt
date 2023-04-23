package com.ls.entertainment.securitylocker.adapter

import android.os.Parcel


class WallpaperModel {
	var id: Int = 0
	var name: String = ""
	var url: String = ""
	var isFromLocal = false
	
	constructor(parcel: Parcel) : this(
		parcel.readInt(),
		parcel.readString().toString(),
		parcel.readString().toString()
	)
	
	constructor(id: Int, name: String, url: String, isFromLocal: Boolean = false) {
		this.id = id
		this.name = name
		this.url = url
		this.isFromLocal = isFromLocal
	}
	
}