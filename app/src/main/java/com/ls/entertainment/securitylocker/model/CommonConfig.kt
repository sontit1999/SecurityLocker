package com.ls.entertainment.securitylocker.model

import com.google.gson.annotations.SerializedName

class CommonConfig {

	@SerializedName(value = "isActiveAds")
	var isActiveAds: Boolean = true

	@SerializedName(value = "supportInter")
	var supportInter: Boolean = true

	@SerializedName(value = "supportNative")
	var supportNative: Boolean = false

	@SerializedName(value = "supportNativeLanguage")
	var supportNativeLanguage: Boolean = true

	@SerializedName(value = "supportReward")
	var supportReward: Boolean = true

	@SerializedName(value = "supportBanner")
	var supportBanner: Boolean = true

	@SerializedName(value = "supportBannerLanguage")
	var supportBannerLanguage: Boolean = false

	@SerializedName(value = "supportBannerCollapseAllScreen")
	var supportBannerCollapseAllScreen: Boolean = true

	@SerializedName(value = "supportOpenAds")
	var supportOpenAds: Boolean = true

	@SerializedName(value = "waitingShowInter")
	var waitingShowInter: Int = 20

	@SerializedName(value = "latestVersion")
	var latestVersion: String = ""
	
	@SerializedName(value = "packageName")
	var packageName: String = "com.ls.entertainment.securitylocker"
	
	@SerializedName(value = "scenarioChangedWallpaper")
	var scenarioChangedWallpaper: String = "24,24,48,72,168" // days
	
	@SerializedName(value = "timeNotify10mPresent")
	var timeNotify10mPresent: Int = 10
	
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

	@SerializedName(value = "numberOfNativeDisplay")
	var numberOfNativeDisplay: Long = 4
	
	@SerializedName(value = "posAddNativeStart")
	var posAddNativeStart: Int = 1
	
	@SerializedName(value = "distanceNativeAd")
	var distanceNativeAd: Int = 6
	
	@SerializedName(value = "supportNativeInLock")
	var supportNativeInLock: Boolean = false
	
	@SerializedName(value = "supportBoostedRam")
	var supportBoostedRam: Boolean = false

	@SerializedName(value = "timeNotifyAfterUnplugInSecond")
	var timeNotifyAfterUnplugInSecond: Int = 3600

	@SerializedName(value = "timeScheduleRestartServiceInSecond")
	var timeScheduleRestartServiceInSecond: Int = 86400

	@SerializedName(value = "countShowOptimizeBattery")
	var countShowOptimizeBattery: Int = 2

	@SerializedName(value = "timePingFakeServer")
	var timePingFakeServer: Long = 300000

	@SerializedName(value = "urlImageIntro")
	var urlImageIntro: String = "https://www.alliancetech.com/wp-content/uploads/2019/07/GettyImages-996865256.jpg"


	@SerializedName(value = "listImage")
	var listImage: String =
		"https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/236_20220909/Design220906009.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/173_20210625/Design210622025.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/14_Other/93_20220812/Others220809010.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/226_20220701/Design220629006.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/222_20220603/Design220531003.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/244_20221104/Design221101001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/08_Drawn_Cartoons/232_20230224/Cartoon230220002.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/04_Auto_Vehicles/158_20210730/Car210727001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/08_Drawn_Cartoons/211_20220708/Cartoon220706001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/05_Love/194_20220826/Love220823001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/173_20210625/Design210618002.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/04_Auto_Vehicles/226_20230310/Car230306002.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/264_20230324/Design230320003.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/03_Pets_Animals/207_20220429/Pet220426004.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/169_20210528/Design210525087.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/08_Drawn_Cartoons/235_20230310/Cartoon230306002.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/247_20221125/Design221121006.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/12_Anime/155_20210305/Anime210302025.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/224_20220617/Design220615027.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/05_Love/212_20230317/Love230313001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/04_Auto_Vehicles/151_20210521/Car210518004.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/03_Pets_Animals/230_20221007/Pet221003001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/08_Drawn_Cartoons/210_20220701/Cartoon220629003.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/169_20210528/Design210525135.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/10_Entertainment_Game/255_20230203/Film230128001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/02_Nature_Landscape/236_20220909/Nature220906001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/03_Pets_Animals/172_20210820/Pet210817003.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/256_20230127/Design230111004.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/04_Auto_Vehicles/143_20210205/Car210128003.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/227_20220708/Design220706022.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/10_Entertainment_Game/247_20221209/Film221205001.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/260_20230224/Design230220003.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/182_20210827/Design210824008.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/247_20221125/Design221121002.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/162_20210409/Design210406054.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/12_Anime/196_20211217/Anime211213020.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/07_Abstract_Designs_3D/249_20221209/Design221205007.jpg????https://wall7stosg.tpwildcardserver.vn/wall7storage/08_Drawn_Cartoons/159_20210521/Cartoon210518007.jpg"

	fun resetConfig(){
		this.isActiveAds = false
		this.supportBanner = false
		this.supportNative = false
		this.supportBoostedRam = false
		this.supportNativeInLock = false
		this.supportReward = false
		this.supportInter = false
	}
}