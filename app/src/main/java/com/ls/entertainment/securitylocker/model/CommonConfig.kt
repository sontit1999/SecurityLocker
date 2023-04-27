package com.ls.entertainment.securitylocker.model

import com.google.gson.annotations.SerializedName

class CommonConfig {

	@SerializedName(value = "isActiveAds")
	var isActiveAds: Boolean = false

	@SerializedName(value = "supportInter")
	var supportInter: Boolean = false

	@SerializedName(value = "supportNative")
	var supportNative: Boolean = false

	@SerializedName(value = "supportReward")
	var supportReward: Boolean = false

	@SerializedName(value = "supportBanner")
	var supportBanner: Boolean = false

	@SerializedName(value = "supportOpenAds")
	var supportOpenAds: Boolean = false

	@SerializedName(value = "waitingShowInter")
	var waitingShowInter: Int = 30

	@SerializedName(value = "waitingShowReward")
	var waitingShowReward: Int = 1

	@SerializedName(value = "waitingShowOpenAds")
	var waitingShowOpenAds: Int = 1

	@SerializedName(value = "latestVersion")
	var latestVersion: String = ""

	@SerializedName(value = "packageName")
	var packageName: String = "com.ls.entertainment.securitylocker"
	
	@SerializedName(value = "scenarioChangedWallpaper")
	var scenarioChangedWallpaper: String = "24,24,48,72,168" // days

	@SerializedName(value = "timeNotify10m")
	var timeNotify10m: Int = 10

	@SerializedName(value = "versionCodeForReview")
	var versionCodeForReview: Int = 0

	@SerializedName(value = "openAdKey")
	var openAdKey: String = "ca-app-pub-4945756407745123/4489203833"

	@SerializedName(value = "bannerAdKey")
	var bannerAdKey: String = "ca-app-pub-4945756407745123/8428448848"

	@SerializedName(value = "interAdKey")
	var interAdKey: String = "ca-app-pub-4945756407745123/1527998570"

	@SerializedName(value = "nativeAdKey")
	var nativeAdKey: String = "ca-app-pub-4945756407745123/8380309398"

	@SerializedName(value = "rewardAdKey")
	var rewardAdKey: String = "ca-app-pub-4945756407745123/1567489033"

	@SerializedName(value = "bannerAdKeyApplovin")
	var bannerAdKeyApplovin: String = "7e9d282524e92738"

	@SerializedName(value = "interAdKeyApplovin")
	var interAdKeyApplovin: String = "be5936464b11788f"

	@SerializedName(value = "nativeAdKeyApplovin")
	var nativeAdKeyApplovin: String = "fcc69dbe4d746e07"

	@SerializedName(value = "rewardAdKeyApplovin")
	var rewardAdKeyApplovin: String = "f3f43a8f1b671292"
	
	@SerializedName(value = "numberOfNativeDisplay")
	var numberOfNativeDisplay: Long = 4
	
	@SerializedName(value = "posAddNativeStart")
	var posAddNativeStart: Int = 1
	
	@SerializedName(value = "distanceNativeAd")
	var distanceNativeAd: Int = 6
	
	@SerializedName(value = "supportNativeInLock")
	var supportNativeInLock: Boolean = false
	
	@SerializedName(value = "listImage")
	var listImage: String =
		"https://images.pexels.com/photos/3052361/pexels-photo-3052361.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/3617500/pexels-photo-3617500.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2670898/pexels-photo-2670898.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/3894157/pexels-photo-3894157.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2832034/pexels-photo-2832034.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1226302/pexels-photo-1226302.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1366630/pexels-photo-1366630.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2770371/pexels-photo-2770371.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2486168/pexels-photo-2486168.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1955134/pexels-photo-1955134.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/3849168/pexels-photo-3849168.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/3214944/pexels-photo-3214944.jpeg?auto=compress&cs=tinysrgb&w=1600https://images.pexels.com/photos/1274260/pexels-photo-1274260.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1624496/pexels-photo-1624496.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1544376/pexels-photo-1544376.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1212487/pexels-photo-1212487.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1334116/pexels-photo-1334116.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/775203/pexels-photo-775203.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1591382/pexels-photo-1591382.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2387417/pexels-photo-2387417.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2953902/pexels-photo-2953902.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2260800/pexels-photo-2260800.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2293372/pexels-photo-2293372.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1743366/pexels-photo-1743366.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/2640024/pexels-photo-2640024.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/1624505/pexels-photo-1624505.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/3717270/pexels-photo-3717270.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/15755456/pexels-photo-15755456.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/15529024/pexels-photo-15529024.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/15968399/pexels-photo-15968399.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/13865993/pexels-photo-13865993.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/15346467/pexels-photo-15346467.jpeg?auto=compress&cs=tinysrgb&w=1600????https://images.pexels.com/photos/15350054/pexels-photo-15350054.jpeg?auto=compress&cs=tinysrgb&w=1600"
	
}