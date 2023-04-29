package com.ls.entertainment.securitylocker.utils

object AllEvents {
	// reward
	const val E1_ADS_REWARD_LOAD_SUCCESS = "e1_ads_reward_load_success"
	const val E1_ADS_REWARD_LOAD_FAIL = "e1_ads_reward_load_fail"
	const val E1_ADS_REWARD_SHOW_SUCCESS = "e1_ads_reward_show_success"
	const val E1_ADS_REWARD_SHOW_FAIL = "e1_ads_reward_show_fail"
	const val E1_ADS_REWARD_CLICKED = "e1_ads_reward_click"
	const val E1_ADS_REWARD_SHOW_FAIL_NO_ADS = "e1_ads_reward_show_fail_no_ads"
	const val E1_ADS_REWARD_USER_EARN_SUCCESS = "e1_ads_reward_user_earn_success"

	// inter
	const val E1_ADS_INTER_LOAD_SUCCESS = "e1_ads_inter_load_success"
	const val E1_ADS_INTER_LOAD_FAIL = "e1_ads_inter_load_fail"
	const val E1_ADS_INTER_SHOW_SUCCESS = "e1_ads_inter_show_success"
	const val E1_ADS_INTER_SHOW_FAIL = "e1_ads_inter_show_fail"
	const val E1_ADS_INTER_CLICKED = "e1_ads_inter_click"
	const val E1_ADS_INTER_SHOW_FAIL_NO_ADS = "e1_ads_inter_show_fail_no_ads"

	// inter applovin
	const val E1_ADS_INTER_APPLOVIN_LOAD_SUCCESS = "e1_ads_inter_applovin_load_success"
	const val E1_ADS_INTER_APPLOVIN_LOAD_FAIL = "e1_ads_inter_applovin_load_fail"
	const val E1_ADS_INTER_APPLOVIN_SHOW_SUCCESS = "e1_ads_inter_applovin_show_success"
	const val E1_ADS_INTER_APPLOVIN_SHOW_FAIL = "e1_ads_inter_applovin_show_fail"
	const val E1_ADS_INTER_APPLOVIN_CLICKED = "e1_ads_inter_applovin_click"
	const val E1_ADS_INTER_APPLOVIN_SHOW_FAIL_NO_ADS = "e1_ads_inter_applovin_show_fail_no_ads"

	// native
	const val E1_ADS_NATIVE_LOAD_SUCCESS = "e1_ads_native_load_success"
	const val E1_ADS_NATIVE_LOAD_FAIL = "e1_ads_native_load_fail"

	// native
	const val E1_ADS_NATIVE_APPLOVIN_LOAD_SUCCESS = "e1_ads_native_applovin_load_success"
	const val E1_ADS_NATIVE_APPLOVIN_LOAD_FAIL = "e1_ads_native_applovin_load_fail"
	const val E1_ADS_NATIVE_APPLOVIN_CLICKED = "e1_ads_native_applovin_click"

	// banner
	const val E1_ADS_BANNER_LOAD_SUCCESS = "e1_ads_banner_load_success"
	const val E1_ADS_BANNER_LOAD_FAIL = "e1_ads_banner_load_fail"
	const val E1_ADS_BANNER_CLICK = "e1_ads_banner_click"

	// banner applovin
	const val E1_ADS_BANNER_APPLOVIN_LOAD_SUCCESS = "e1_ads_banner_applovin_load_success"
	const val E1_ADS_BANNER_APPLOVIN_LOAD_FAIL = "e1_ads_banner_applovin_load_fail"
	const val E1_ADS_BANNER_APPLOVIN_CLICK = "e1_ads_banner_applovin_click"

	// open ads
	const val E1_ADS_OPEN_ADS_LOAD_SUCCESS = "e1_ads_open_load_success"
	const val E1_ADS_OPEN_ADS_LOAD_FAIL = "e1_ads_open_load_fail"
	const val E1_ADS_OPEN_ADS_SHOW_SUCCESS = "e1_ads_open_show_success"
	const val E1_ADS_OPEN_ADS_SHOW_FAIL = "e1_ads_open_show_fail"
	const val E1_ADS_OPEN_ADS_CLICKED = "e1_ads_open_click"
	const val E1_ADS_OPEN_ADS_SHOW_FAIL_NO_ADS = "e1_ads_open_show_fail_no_ads"
	
	// other
	const val E1_OPEN_USER_FIRST_OPEN = "e1_open_user_first_open"
	const val E1_OPEN_USER_REOPEN = "e1_open_user_reopen"
	const val E1_NOTIFICATION_FCM_RECEIVE = "e1_notification_fcm_receive"
	const val E1_NOTIFICATION_FCM_CLICK = "e1_notification_fcm_click"
	const val E1_NOTIFICATION_OFFLINE_RECEIVE = "e1_notification_offline_receive"
	const val E1_NOTIFICATION_OFFLINE_CLICK = "e1_notification_offline_click"
	const val E1_CONFIG_LOAD_SUCCESS = "e1_config_load_success"
	const val E1_CONFIG_LOAD_FAIL = "e1_config_load_fail"
	const val E1_CLICK_UPDATE_APP = "e1_click_update_app"
	
	// action download
	const val DOWNLOAD_WALLPAPER_SUCCESS = "download_success"
	const val DOWNLOAD_WALLPAPER_FAIL = "download_fail"
	
	// action set
	const val SET_WALLPAPER_LOCK_SUCCESS = "set_wall_lock_success"
	const val SET_WALLPAPER_LOCK_FAIL = "set_wall_lock_fail"
	
	const val SET_WALLPAPER_SUCCESS = "set_wall_success"
	const val SET_WALLPAPER_FAIL = "set_wall_fail"
	
	// action user
	const val ACTION_ALLOW_USAGE_DIALOG = "action_press_allow_usage"
	const val ACTION_ALLOW_OVERLAY_DIALOG = "action_press_allow_overlay"
	const val ACTION_ALLOW_WRITE_SETTING_DIALOG = "action_press_allow_write_setting"
	const val ACTION_SEARCH = "action_search"
	const val ACTION_LOCK = "action_lock_app"
	const val ACTION_INFORMATION = "action_information_app"
	const val ACTION_UNINSTALL = "action_uninstall"
	const val ACTION_SWIPE_DETAIL = "action_swipe_detail"
	const val ACTION_CHANGE_PREVIEW = "action_change_preview"
	const val ACTION_BACK_DETAIL = "action_back_detail"
	const val ACTION_DOWNLOAD_WALLPAPER = "action_download_wallpaper"
	const val ACTION_ACCEPT_DOWNLOAD = "action_download_accept"
	const val ACTION_DENY_DOWNLOAD = "action_deny_accept"
	const val ACTION_SET_LOCK_APP = "action_set_wall_lock_app"
	const val ACTION_SET_HOME_SCREEN = "action_set_wall_home_screen"
	const val ACTION_SET_LOCK_SCREEN = "action_set_wall_lock_screen"
	const val ACTION_UPDATE = "action_update"
	const val ACTION_FEEDBACK = "action_feedback"
	const val ACTION_SHARE = "action_share"
	const val ACTION_RATE = "action_rate"
	const val ACTION_POLICY = "action_policy"
	const val ACTION_CHANGE_PASS = "action_change_pass"
	const val ACTION_UNLOCK_MANAGE_APP = "action_unlock_manage_app"
	const val ACTION_UNLOCK_CHANGE_THEME = "action_unlock_change_theme"
	const val ACTION_UNLOCK_SETTING = "action_unlock_setting"
	const val ACTION_UNLOCK_SUCCESS = "action_unlock_success"
	const val ACTION_UNLOCK_FAIL = "action_unlock_fail"
	const val ACTION_ACCEPT_BATTERY_SAVER = "action_accept_battery_saver"
	const val ACTION_DENY_BATTERY_SAVER = "action_deny_battery_saver"
	const val ACTION_CLICK_OPEN_WIFI = "action_click_open_wifi"
	
	// permission
	const val PERMISSION_USAGE_ACCEPT = "permission_usage_accept"
	const val PERMISSION_USAGE_DENY = "permission_usage_deny"
	const val PERMISSION_OVERLAY_ACCEPT = "permission_overlay_accept"
	const val PERMISSION_OVERLAY_DENY = "permission_overlay_deny"
	const val PERMISSION_WRITE_SETTING_ACCEPT = "permission_write_setting_accept"
	const val PERMISSION_WRITE_SETTING_DENY = "permission_write_setting_deny"
	const val PERMISSION_STORAGE_ACCEPT = "permission_storage_accept"
	const val PERMISSION_STORAGE_DENY = "permission_storage_deny"
	
	// view
	const val VIEW_SPLASH = "view_splash"
	const val VIEW_ALL_APP = "view_all_app"
	const val VIEW_LOCK_SETUP = "view_lock_setup"
	const val VIEW_TOOL = "view_tool"
	const val VIEW_THEME = "view_theme"
	const val VIEW_SETTING = "view_setting"
	const val VIEW_DETAIL = "view_detail"
	const val VIEW_CHANGE_PASSWORD = "view_change_pass"
	const val VIEW_POLICY = "view_policy"
	const val VIEW_UNLOCK_SCREEN = "view_unlock"
	const val VIEW_BATTERY_SAVER = "view_battery_saver"
	const val VIEW_UPDATE_APP = "view_update_app"
	const val VIEW_NO_NETWORK = "view_no_network"
	
	// service and broadcast
	const val LOCK_SERVICE_ON_CREATE = "service_lock_on_create"
	const val LOCK_SERVICE_ON_START_COMMAND = "service_lock_on_start_command"
	const val LOCK_SERVICE_ON_TASK_REMOVE = "service_lock_on_task_remove"
	const val LOCK_SERVICE_ON_DESTROY = "service_lock_on_destroy"
	const val LOCK_SERVICE_PING_FAKE_SERVER = "service_lock_ping_fake_server"
	const val LOCK_SERVICE_BROADCAST_RECEIVE_ACTION = "service_lock_broadcast_receive_action_"
	const val BOOT_RAM_SERVICE_SUCCESS = "worker_booster_ram_success"
	const val BOOT_RAM_SERVICE_FAIL = "worker_booster_ram_fail"
	const val ALARM_BROADCAST_RECEIVE_ACTION = "alarm_broadcast_receive_action_"
	const val REBOOT_BROADCAST_RECEIVE_ACTION = "reboot_broadcast_receive_action_"
	const val POWER_BROADCAST_RECEIVE_ACTION = "power_broadcast_receive_action_"
	const val SCREEN_BROADCAST_RECEIVE_ACTION = "screen_broadcast_receive_action_"
	const val UPDATE_BROADCAST_RECEIVE_ACTION = "update_broadcast_receive_action_"
	const val WORKER_10M_AFTER_PRESENT = "worker_10m_present_do_work"
	const val WORKER_OFFLINE = "worker_notification_offline_do_work"
	const val WORKER_ONE_HOUR_AFTER_UNPLUG = "worker_one_hour_after_unplug_do_work"
	const val WORKER_POWER_RESTART_SERVICE = "worker_power_restart_service_do_work"
	const val WORKER_RESTART_SERVICE_EVERY_DAY = "worker_restart_service_everyday_do_work"
}