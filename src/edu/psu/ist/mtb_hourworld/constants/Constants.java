package edu.psu.ist.mtb_hourworld.constants;

public class Constants {
	
	// check the app version
	public final static String GET_APP_VERSION = "http://community.ist.psu.edu/version/get_app_version_tb.php";
	
	// facebook graph
	public final static String FACEBOOK_GRAPH = "https://graph.facebook.com/";
	
	// misc.
	public final static String GCM_REGISTRATION = "http://cscl.ist.psu.edu/civicinity/mobile_handler/c2dm_php_server/registration_tb.php";
	
	// GCM
	public final static String PROJECT_ID_GCM 	= "271816562185";
	
	// profile
	public final static String GET_USER_PROFILE_INFO 		= "http://cscl.ist.psu.edu/civicinity/mobile_handler/timebank/get_user_profile_info.php";
	public final static String GET_USER_PROFILE_INFO_NEW 	= "http://cscl.ist.psu.edu/civicinity/mobile_handler/timebank/download_profile.php";
	
	// Google place API
	public final static String LOCAL_SEARCH_URL 		= "http://ajax.googleapis.com/ajax/services/search/local";
	public final static String API_KEY					= "AIzaSyD5spwHUyj4Rnyc34SyQG9EaDqd_uN5EzI";
	public final static String REQUEST_PLACE_API 		= "https://maps.googleapis.com/maps/api/place/search/json?";
	public final static String REQUEST_REFERENCE_API 	= "https://maps.googleapis.com/maps/api/place/details/json?reference=";
	
	// sd card path
	public static final String MTB_ROOT_PATH 			= "/sdcard/timebank/";
	public static final String MTB_PHOTO_PATH 			= "/sdcard/timebank/photos/";
	public static final String MTB_FILE_PATH 			= "/sdcard/timebank/files/";
	public static final String MTB_CACHES 				= "/sdcard/timebank/caches/";
	
	// default lat, lng
	public static final double DEFAULT_LAT = 40.793831;
	public static final double DEFAULT_LNG = -77.868218;
	
	// touch movement
	public final static int RIGHT_TO_LEFT = 1;
	public final static int LEFT_TO_RIGHT = 2;
	
	// maintaining two contents table for the backup
	public final static int MAIN_RSSContents = 0;
	public final static int TEMP_RSSContents = 1;
	
	// 20 Jan 2000 12:00:00 -0400
	public final static int DAY_FORMAT = 1;
	public final static int MONTH_FORMAT = 2;
	public final static int YEAR_FORMAT = 3;
	public final static int TIME_FORMAT = 4;
		
	// previous activity
	public final static int PREV_ACITIVITY_ADD_NEW = 0;
	public final static int PREV_ACITIVITY_EDIT = 1;	
	
	// C2DM setup
	public final static String DEVELOPER_EMAIL = "civicinity.mobile@gmail.com";
	
	// login prevActivity
	public final static int FROM_BEGINNING = 0;
	public final static int FROM_OTHERS = 1;
	public final static int FINISH = 2;	
	
	
	// menu constant
	public final static int FROM_MESSAGES = 0;
	public final static int FROM_OFFERS = 1;
	public final static int FROM_REQUESTS = 2;
	public final static int FROM_SEARCH = 3;
	public final static int FROM_GROUP = 4;
	public final static int FROM_MORE = 5;
	public final static int FROM_SETTINGS = 6;
	
	// startActivityForResult
	public final static int SIDEBAR_MENU = 100;
	public final static int MTB_DETAIL_PAGE = 101;
	
	
	/*
	 * Hourworld
	 */
	public final static String AUTHENTICATE = "http://www.hourworld.org/db_mob/auth.php";
	public final static String HOURWORLD = "https://hourworld.org/";
	public final static String EXCHANGE = "http://www.hourworld.org/db_mob/onLoad.php";
	public final static String JOIN = "http://www.hourworld.org/db_mob/onJoin.php";
}
