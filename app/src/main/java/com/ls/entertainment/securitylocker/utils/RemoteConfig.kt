package com.ls.entertainment.securitylocker.utils

import com.ls.entertainment.securitylocker.model.ConfigModel

object RemoteConfig {
	var configModel = ConfigModel()

	val commonConfig
		get() = configModel.commonInfo
}