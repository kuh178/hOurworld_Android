package edu.psu.ist.mtb_hourworld.location;

import java.util.Timer;
import java.util.TimerTask;

import edu.psu.ist.mtb_hourworld.constants.Constants;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;

public class GPSHandler extends Service{

	private double mLatitude 		= Constants.DEFAULT_LAT;
	private double mLongitude 		= Constants.DEFAULT_LNG;
	private float mAccuracy;
	private Timer mTimer = new Timer();
	private final IBinder mBinder = new GpsBinder();
	private LocationManager mLocManager;
	
	public static final int OUT_OF_SERVICE = 0;
	public static final int TEMPORARILY_UNAVAILABLE = 1;
	public static final int AVAILABLE = 2;
	
	public static final String GPSHANDLERFILTER = "devs.own.intent.filter";
	public static final long TIME_LIMIT = 20 * 1000;
	
	//private MobileTimeBankingDatabase mDB;
	
	/** this criteria will settle for less accuracy, high power, and cost */
	public static Criteria createCoarseCriteria() {
		// network
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_COARSE);
		//c.setAltitudeRequired(false);
		//c.setBearingRequired(false);
		//c.setSpeedRequired(false);
		//c.setCostAllowed(false);
		c.setPowerRequirement(Criteria.POWER_HIGH);
		return c;
	}
	 
	/** this criteria needs high accuracy, high power, and cost */
	public static Criteria createFineCriteria() {
		// GPS
		Criteria c = new Criteria();
		c.setAccuracy(Criteria.ACCURACY_FINE);
		//c.setAltitudeRequired(false);
		//c.setBearingRequired(false);
		//c.setSpeedRequired(false);
		//c.setCostAllowed(false);
		c.setPowerRequirement(Criteria.POWER_HIGH);
		return c;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		
		Log.i("K", "service!");

		//mDB = new MobileTimeBankingDatabase(this);
		mLocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

		// get low accuracy provider
		LocationProvider low=
			mLocManager.getProvider(mLocManager.getBestProvider(createCoarseCriteria(), false));
		// get high accuracy provider
		LocationProvider high=
			mLocManager.getProvider(mLocManager.getBestProvider(createFineCriteria(), false));
		
		//Location lastLocFromNetwork = mLocManager.getLastKnownLocation(low.getName());
		//Location lastLocFromGPS = mLocManager.getLastKnownLocation(high.getName());
		
		if(mLocManager.isProviderEnabled(low.getName())) {
			// using low accuracy provider... to listen for updates
			Log.i("K", "NETWORK enabled!");
			mLocManager.requestLocationUpdates(low.getName(), 10 * 1000, 0, lowListener);
		}
		if(mLocManager.isProviderEnabled(high.getName())) {
			// using high accuracy provider... to listen for updates
			Log.i("K", "GPS enabled!");
			mLocManager.requestLocationUpdates(high.getName(), 10 * 1000, 0, highListener);
		}
		
		mTimer.schedule(new GetLastLocation(), TIME_LIMIT);
	}
	
	private LocationListener lowListener = new LocationListener() {
        public void onLocationChanged(Location location) {
        	// do something here to save this new location
        	mLatitude = location.getLatitude();
        	mLongitude = location.getLongitude();
        	mAccuracy = location.getAccuracy();
				
        	Log.i("K", "LOW latitude: " + mLatitude + " / longitude: " + mLongitude + " / accuracy : " + mAccuracy);
        	//mDB.setLostStateCollegeGPS(mLatitude, mLongitude);	
        	
        	broadCast();
        }
        public void onStatusChanged(String provider, int status, Bundle bundle) {
        	Log.i("K", status + " , " + provider.toString());
        }
        public void onProviderEnabled(String s) {
        	Log.i("K", s.toString());
        }
        public void onProviderDisabled(String s) {
        	Log.i("K", s.toString());
        }
	};
	
	private LocationListener highListener = new LocationListener() {
        public void onLocationChanged(Location location) {
	        // do something here to save this new location
    		mLatitude = location.getLatitude();
			mLongitude = location.getLongitude();
			mAccuracy = location.getAccuracy();
			
			Log.i("K", "HIGH latitude: " + mLatitude + " / longitude: " + mLongitude + " / accuracy : " + mAccuracy);
			//mDB.setLostStateCollegeGPS(mLatitude, mLongitude);
			
			broadCast();
        }
        public void onStatusChanged(String s, int i, Bundle bundle) {}
        public void onProviderEnabled(String s) {}
        public void onProviderDisabled(String s) {}
	};
	      
	      
	private void removeLocListener() {
		mLocManager.removeUpdates(lowListener);
		mLocManager.removeUpdates(highListener);
	}
	
	class GetLastLocation extends TimerTask {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			Log.i("K", "HelperGpsHandler- Time Limit 15 sec passed, fetch default values.");
		
			// send the last broadcast
			Intent intent = new Intent();
			
			intent.putExtra("latitude", Constants.DEFAULT_LAT);
			intent.putExtra("longitude", Constants.DEFAULT_LNG);
			intent.setAction(GPSHANDLERFILTER); // define intent-filter
			
			Log.i("K", "DEFAULT latitude: " + mLatitude + " / longitude: " + mLongitude);
			
			//mDB.setLostStateCollegeGPS(mLatitude, mLongitude);
			sendBroadcast(intent);
			
			//onDestroy();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i("K", "Destory GPSHandler()");
//		if(mLocManager != null) {
			//mLocManager.removeUpdates(this);
//			mLocManager = null;
//		}
		//stopSelf();
		
		removeLocListener();
	}
	
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return mBinder;
	}
	
	private void broadCast() {
		
		mTimer.cancel();
		
		SharedPreferences mPref = PreferenceManager.getDefaultSharedPreferences(this);
		SharedPreferences.Editor editor = mPref.edit();
		editor.putString("latitude", String.valueOf(mLatitude));
		editor.putString("longitude", String.valueOf(mLongitude));
		editor.commit();
		
		Log.i("K", "Location received:" + mLatitude + " , " + mLongitude);
		
		Intent intent = new Intent();
		intent.putExtra("latitude", mLatitude);
		intent.putExtra("longitude", mLongitude);
		intent.putExtra("accuracy", mAccuracy);
		intent.setAction(GPSHANDLERFILTER); // define intent-filter
		sendBroadcast(intent);
	}
	
	public class GpsBinder extends Binder {
		public GPSHandler getService() {
			return GPSHandler.this;
		}
	}
}

