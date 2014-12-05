package edu.psu.ist.mtb_hourworld;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.crashlytics.android.Crashlytics;
import com.google.analytics.tracking.android.EasyTracker;

import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.location.GPSHandler;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

	private SharedPreferences mPref;
	private LocationManager mManager;
	private double mLatitude 		= 0.0;
	private double mLongitude 		= 0.0;

	private boolean mIsBound;
	private boolean mIsNewestVersion = false;
	
	private String android_id;
	
	private ServiceConnection mConnection = new ServiceConnection() {

		public void onServiceConnected(ComponentName className, IBinder binder) {
			((GPSHandler.GpsBinder) binder).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
		}
	};

	private void doBindService() {
		Log.i("K", "BindService");

		bindService(new Intent(MainActivity.this, GPSHandler.class), mConnection, Context.BIND_AUTO_CREATE);
		mIsBound = true;	
	}

	private void doUnbindService() {

		if(mIsBound) {
			if(mConnection != null) {

			}
			unbindService(mConnection);
			mIsBound = false;
		}
	}
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mtb_main);
        
        mPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        
        // add android_id
        android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
        
        SharedPreferences.Editor editor = mPref.edit();
		editor.putString("android_id", android_id);
		editor.commit();
		
		// crashlytics
		Crashlytics.start(this);
        
		// get the version from the server.
		new versionTest().execute();
		
		//startActivity();
    }
    
    @Override
    public void onStart() {
    	super.onStart();
    	EasyTracker.getInstance(this).activityStart(this); // Add this method.
    }

    @Override
    public void onStop() {
    	super.onStop();
    	EasyTracker.getInstance(this).activityStop(this); // Add this method.
    }
    
    
    class versionTest extends AsyncTask<Void, Void, Boolean> {

    	//ProgressDialog dialog;
    	Dialog dialog = new Dialog(MainActivity.this);
    	
    	@Override
    	protected void onPreExecute() {
    		dialog.setContentView(R.layout.mtb_version_test_dialog);
    		dialog.setTitle("Loading hOurworld mobile...");
    		dialog.show();
    	}
    	
		@Override
		protected Boolean doInBackground(Void... arg0) {
			
			HttpClient httpClient = new DefaultHttpClient();
			HttpPost httpPost = new HttpPost(Constants.GET_APP_VERSION);
			String result = "";
			int versionCodeFromServer = 0;
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					
					StringBuilder result_str = new StringBuilder();
					for(;;){
						String line = br.readLine();
						if (line == null) 
							break;
						result_str.append(line+'\n');
					}
					
					result = result_str.toString();
					
					JSONObject jObj = new JSONObject(result);
					
					if(jObj.getBoolean("success")){
						JSONObject jResult = jObj.getJSONObject("results");
						
						versionCodeFromServer = jResult.getInt("version_code");
					}
					else {
						return false;
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// check if the app needs updates
	        try {
				PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
				int versionCode = pInfo.versionCode;
				
				Log.i("K", "versionCodeFromServer : " + versionCodeFromServer + " versionCode : " + versionCode);
				
				if(versionCode < versionCodeFromServer) {
					return true;
				}
				
			} catch (NameNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 
			
			return false;
		}
		
		protected void onPostExecute(Boolean returnVal) {
			
			if(dialog.isShowing()) {
				dialog.dismiss();
			}
			
			// if the return value is true, ask if a user wants to upgrade the app.
			if(returnVal) {
	
				mIsNewestVersion = false;
				
				new AlertDialog.Builder(MainActivity.this)
				.setTitle(getString(R.string.update_request_title))
				.setMessage(getString(R.string.update_request_message))
				//.setIcon(R.drawable.hourworld_icon_30_30)
				.setPositiveButton(getString(R.string.update_request_title), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						
						Intent intent = new Intent();
				        intent.setAction(Intent.ACTION_VIEW);
				        intent.addCategory(Intent.CATEGORY_BROWSABLE);
				        intent.setData(Uri.parse("market://details?id=edu.psu.ist.mtb_hourworld"));
				        startActivity(intent);
					}
				})
				.setNegativeButton(getString(R.string.update_request_later), new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// handling locationManager
						mManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

						// register Receiver
						IntentFilter inFilter = new IntentFilter(GPSHandler.GPSHANDLERFILTER);
						registerReceiver(gpsReceiver, inFilter);

						// temporary disable this
						// checkGpsIsOn();
					}
				})
				.show();
			}
			else {
				
				mIsNewestVersion = true;
				//C2DM registerID issue

				// create the file directory if there's nothing.
		        File file = new File(Constants.MTB_ROOT_PATH);
				if(!file.exists()) {
					file.mkdir();
				}
				
				file = new File(Constants.MTB_FILE_PATH);
				if(!file.exists()) {
					file.mkdir();
				}
				
		        file = new File(Constants.MTB_CACHES);
				if(!file.exists()) {
					file.mkdir();
				}
				

				// handling locationManager
				mManager = (LocationManager) MainActivity.this.getSystemService(Context.LOCATION_SERVICE);

				// register Receiver
				IntentFilter inFilter = new IntentFilter(GPSHandler.GPSHANDLERFILTER);
				registerReceiver(gpsReceiver, inFilter);

				// temporary disable this
				//checkGpsIsOn();
				checkAccountExpiration();
				//startActivity();
			}
			
			/*
			// check if the user credential is still valid (or not expired yet)
			
			*/
		}
    	
    }
    
    public void checkAccountExpiration() {
    	
    	Log.i("K", "access token: " + mPref.getString("access_token", ""));
    	
    	if(mPref.getString("access_token", "").equals("") || mPref.getString("access_token", "").equals(null)) {
    		// move to login
    		Intent intent = new Intent(MainActivity.this, MTBLoginPage.class);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
	    	finish();
    	}
    	else if(checkIfUserLoggedIn()) {
    		// main page
			Intent intent = new Intent(MainActivity.this, MTBMainMenuPage.class);
        	intent.putExtra("terminate", false);
        	startActivity(intent);
        	finish();
		}
		else {
			new AlertDialog.Builder(MainActivity.this)
			.setTitle("Message")
			.setIcon(R.drawable.hourworld_icon_30_30)
			.setMessage("Your account has been expired. Please login again.")
			.setPositiveButton("Login", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					// clear sharedPreferences
					Editor editor = mPref.edit();
				    editor.clear();
				    editor.commit();
					
					// logoutn
					Intent intent = new Intent(MainActivity.this, MTBLoginPage.class);
		        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
		        	startActivity(intent);
			    	finish();
				}
			})
			.show();
		}
    }
    
    public boolean checkIfUserLoggedIn() {
    	
    	checkUserStatus check = new checkUserStatus();
		try {
			return check.execute().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
    	
		return false;
	}
    
    class checkUserStatus extends AsyncTask<Void, Integer, Boolean>{

    	//ProgressDialog dialog;
    	Dialog dialog = new Dialog(MainActivity.this);
    	
    	@Override
    	protected void onPreExecute() {
    		dialog.setContentView(R.layout.mtb_version_test_dialog);
    		dialog.setTitle("Checking user account...");
    		dialog.show();
    	}
    	
		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("Messages,0")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	//entity.addPart("accessToken", new StringBody("123456")); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
	        	Log.i("K", mPref.getString("access_token", "") + " / " + Integer.toString(mPref.getInt("EID", 0)) + " / " + Integer.toString(mPref.getInt("memID", 0)));
		    	
		    	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		   
			httpPost.setEntity(entity);

			// output string
			String result = "";
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
			
				Log.i("K", "getStatusCode : " + response.getStatusLine().getStatusCode());
				
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
					
					StringBuilder result_str = new StringBuilder();
					for(;;){
						String line = br.readLine();
						if (line == null) 
							break;
						result_str.append(line+'\n');
					}
					result = result_str.toString();
					JSONObject jObj = new JSONObject(result);
				
					Log.i("K", "results: " + result);
				
					if(!jObj.getBoolean("success")){
						return false;
					}
					else {
						return true;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return true;
		}
		
		protected void onPostExecute(Boolean returnVal) {
			if(!dialog.equals(null) && dialog.isShowing()) {
				dialog.dismiss();
			}
		}
	}
    
	// get updated GPS data by the broadcast receiver
	private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			Bundle extras = intent.getExtras();

			mLatitude = extras.getDouble("latitude");
			mLongitude = extras.getDouble("longitude");
			
			Log.i("K", "GPS received : " + mLatitude + ", " + mLongitude);
			
			SharedPreferences.Editor editor = mPref.edit();
			editor.putString("latitude", String.valueOf(mLatitude));
			editor.putString("longitude", String.valueOf(mLongitude));	        
			editor.commit();
			
			doUnbindService();
		}	
	};
	
	public void onPause() {
		super.onPause();
		//Log.i("K", "unregister receiver");
		//unregisterReceiver(gpsReceiver);
	}
	
	public void onDestroy() {
		super.onDestroy();
	
		if(mIsNewestVersion) {
			doUnbindService();
			
			if(gpsReceiver != null) {
				unregisterReceiver(gpsReceiver);
			}
		}
	}
	
	private void startActivity() {
		// bind Service
		doBindService();
		
		// check if GPS data is stored.
        if(Double.parseDouble(mPref.getString("latitude", "0.0")) == 0.0
        		|| Double.parseDouble(mPref.getString("longitude", "0.0")) == 0.0) {
        	
        	new getInitialGeoPoint().execute();
        }
        else {
        	// if not logged in yet...
            if(!mPref.getBoolean("loggedin", false)) {
            	Intent intent = new Intent(MainActivity.this, MTBLoginPage.class);
            	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
            	startActivity(intent);
            	
            	finish();
            }
            // if already authenticated, go to the mainpage
            else {
            	//Intent intent = new Intent(this, MTBMainMenuPage.class);
            	//intent.putExtra("terminate", false);
            	//startActivity(intent);
            	
            	//finish();
            }
        }
	}
	
    private void checkGpsIsOn() {
		// check if GPS is turned on...
		if (mManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			startActivity();
		}
		else {

		 new AlertDialog.Builder(MainActivity.this)
		   		.setTitle(getString(R.string.turn_on_gps_title))
		   		.setMessage(getString(R.string.turn_on_gps_message))
		   		.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
		   			public void onClick(DialogInterface dialog, int whichButton) {
		   				Intent intent = new Intent(Settings.ACTION_SECURITY_SETTINGS);
		   				startActivityForResult(intent, 1);
		   			}
		   		})
		   		.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
		   			public void onClick(DialogInterface dialog, int whichButton) {
		   				finish();
		   			}
		   		})
		   		.show();
		}	
	}
    
    @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

    	if (requestCode == 1) {
    		// after turning on the GPS 
			startActivity();
		}
    	else if (requestCode == 2) {
    		// after finishing the survey
    		
    		// dont show the intro page anymore
			SharedPreferences.Editor editor = mPref.edit();
			editor.putBoolean("show_intro_page", false);
			editor.commit();
			
			// get the version from the server.
			new versionTest().execute();
    	}
	}
  
	private class getInitialGeoPoint extends AsyncTask<Void, Void, Void> {
		
		private ProgressDialog mGPSDialog;
		
		@Override
	    protected void onPreExecute() {

	    	mGPSDialog = new ProgressDialog(MainActivity.this);
	    	mGPSDialog.setTitle(getString(R.string.get_location_title));
	    	mGPSDialog.setMessage(getString(R.string.get_location_message));
	    	mGPSDialog.setCancelable(true);
	    	mGPSDialog.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface arg0) {
					
					mGPSDialog.dismiss();
					Toast.makeText(MainActivity.this, getString(R.string.location_not_detected_message), Toast.LENGTH_SHORT).show();
					
					if(!mPref.getBoolean("loggedin", false)) {
						Intent intent = new Intent(MainActivity.this, MTBLoginPage.class);
		            	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
		            	startActivity(intent);
		            	finish();
					}
					else {
						Intent intent = new Intent(MainActivity.this, MTBMainMenuPage.class);
		            	intent.putExtra("terminate", false);
		            	startActivity(intent);
		            	finish();
					}
				}
	    	});
	    	mGPSDialog.show();
	    }
		
		
		
	    /** The system calls this to perform work in a worker thread and
	      * delivers it the parameters given to AsyncTask.execute() */
		@Override
	    protected Void doInBackground(Void... Void) {
	    	
	    	while(true) {
	    		if(mLatitude != 0.0 && mLongitude != 0.0) {
	    			
	    			Log.i("K", "Get GPS data. Getg out of this loop!");
	    			
	    			return null;
	    		}
	    	}
	    }
	    
	    /** The system calls this to perform work in the UI thread and delivers
	      * the result from doInBackground() */
	    @Override
	    protected void onPostExecute(Void unused) {
	    	
	    	if(mGPSDialog.isShowing()) {
	    		mGPSDialog.dismiss();
	    	}

            if(!mPref.getBoolean("loggedin", false)) {
            	Intent intent = new Intent(MainActivity.this, MTBLoginPage.class);
            	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
            	startActivity(intent);
            	finish();
            }
            else {
            	Intent intent = new Intent(MainActivity.this, MTBLoginPage.class);
            	intent.putExtra("terminate", false);
            	startActivity(intent);
            	finish();
            }
    	    
    	    /**
    	     * Start service whenever app starts
    	     */
	        SharedPreferences.Editor editor = mPref.edit();
			editor.putString("latitude", String.valueOf(mLatitude));
			editor.putString("longitude", String.valueOf(mLongitude));	        
			editor.commit();
	    }
	}
}
