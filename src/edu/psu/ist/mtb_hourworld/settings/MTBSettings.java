package edu.psu.ist.mtb_hourworld.settings;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.android.gcm.GCMRegistrar;


import edu.psu.ist.mtb_hourworld.MTBAboutPage;
import edu.psu.ist.mtb_hourworld.MTBMainMenuPage;
import edu.psu.ist.mtb_hourworld.MTBMessagePage;
import edu.psu.ist.mtb_hourworld.MTBOfferPage;
import edu.psu.ist.mtb_hourworld.MTBRequestPage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.group.MTBGroupMainPage;
import edu.psu.ist.mtb_hourworld.profile.MTBProfilePage;
import edu.psu.ist.mtb_hourworld.search.MTBSearchMainPage;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MTBSettings extends Activity {

	private ListView mListView;
	private SharedPreferences mPref;
	private Button menuBtn;
	
	private TextView mMenuText;
	
	private ArrayList<SettingItem> mArr = new ArrayList<SettingItem>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_settings);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    setTitle("More");
	    getActionBar().setIcon(R.drawable.more);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);

	    mListView = (ListView)findViewById(R.id.listview);
	    menuBtn = (Button)findViewById(R.id.menu_btn);
	    
	    SettingItem sItem = new SettingItem();
	    //sItem.name = "Clear cache";
	    //mArr.add(sItem);
	    
	    sItem = new SettingItem();
	    sItem.name = "My group";
	    mArr.add(sItem);
	    
	    sItem = new SettingItem();
	    sItem.name = "My profile";
	    mArr.add(sItem);
	    
	    sItem = new SettingItem();
	    sItem.name = "About hOurworld";
	    mArr.add(sItem);
	    
	    sItem = new SettingItem();
	    sItem.name = "About the app";
	    mArr.add(sItem);
	    
	    sItem = new SettingItem();
	    sItem.name = "Share the app";
	    mArr.add(sItem);
	    
	    //sItem = new SettingItem();
	    //sItem.name = "Logout";
	    //mArr.add(sItem);
	    
	    try {
			String versionName = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			
			sItem = new SettingItem();
			sItem.name = "Ver. " + versionName;
			mArr.add(sItem);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   
	    
	    
	    TBSettingsAdapter tbAdapter = new TBSettingsAdapter(this, R.layout.mtb_settings_item);
	    mListView.setAdapter(tbAdapter);
	    
	    mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				/*
				if(position == 0) {
					// clearing cache
					new AlertDialog.Builder(MTBSettings.this)
			   		.setTitle("Memory Management")
			   		.setMessage("Clear cache?")
			   		.setIcon(R.drawable.hourworld_icon_30_30)
			   		.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
			   			public void onClick(DialogInterface dialog, int whichButton) {
			   				
			   				new DeleteCache().execute();
			   			}
			   		})
			   		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			   			public void onClick(DialogInterface dialog, int whichButton) {
			   			}
			   		})
			   		.show();
				}
				*/
				if(position == 0) {
					startActivity(new Intent(MTBSettings.this, MTBGroupMainPage.class));
				}
				else if(position == 1) {
					// profile page
					Intent intent = new Intent(MTBSettings.this, MTBProfilePage.class);
					intent.putExtra("userID", mPref.getInt("memID", 0));
					
					startActivity(intent);
				}
				else if(position == 2) {
					// about page
					startActivity(new Intent(MTBSettings.this, MTBAboutPage.class));
				}
				else if(position == 3) {
					// credit page
					startActivity(new Intent(MTBSettings.this, MTBCreditPage.class));
				}
				else if(position == 4) {
					// share the app page
					Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND); 
				    sharingIntent.setType("text/html");
				    
				    //@"Hello,<br><br>I want to share this great hOurworld mobile app with you. Download the app now and earn an hour credit!<br><br>Are you an iOS user? Click <a href=\"https://itunes.apple.com/us/app/hourworld/id671499452?mt=8\">here</a> to download!<br><br>Are you an Android user? Click <a href=\"https://play.google.com/store/apps/details?id=edu.psu.ist.mtb_hourworld&hl=en\">here</a> to download!";
				    
				    String shareBody = "Hello,<br><br>I want to share this great hOurworld mobile app with you. Download the app now and earn an hour credit!<br><br>Are you an Android user? "
				    		+ "Click <a href=\"https://play.google.com/store/apps/details?id=edu.psu.ist.mtb_hourworld&hl=en\">here</a> to download!<br><br>Are you an iOS user? Click <a href=\"https://itunes.apple.com/us/app/hourworld/id671499452?mt=8\">here</a> to download!<br><br>"
				    		+ "If you're not a timebank member, go to the \"Join\" tab here: <a pref=\"http://www.hourworld.org\">www.hourworld.org</a> and select your nearest exchange. "
				    		+ "You must do this for the mobile app to work. Please be patient after signing up. The administrator will contact you to confirm your identity.";
				    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Download the hOurworld mobile app now!");
				    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, Html.fromHtml(shareBody));
				    startActivity(Intent.createChooser(sharingIntent, "Share via"));
				}
				/*
				else if(position == 4) {
					// logging out
					new AlertDialog.Builder(MTBSettings.this)
			   		.setTitle("Logout")
			   		.setIcon(R.drawable.hourworld_icon_30_30)
			   		.setMessage("Proceed to Logout?")
			   		.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
			   			public void onClick(DialogInterface dialog, int whichButton) {
			   				
			   				// go back to the login page
				    		//Intent intent = new Intent(MobileTimeBankingPreferences.this, MobileTimeBankingMainPage.class);
					        //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
					        //intent.putExtra("terminate", true);
							//startActivity(intent);

							//MobileTimeBankingPreferences.this.finish();
			   				
			   				// upload the logout to the server
			   				new logoutFromApp().execute();
			   			}
			   		})
			   		.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			   			public void onClick(DialogInterface dialog, int whichButton) {
			   			}
			   		})
			   		.show();
				}
				else {
					
				}
				*/
			}
	    });
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onStart() {
	    super.onStart();
	    EasyTracker.getInstance(this).activityStart(this);  // Add this method.
	}

	@Override
	public void onStop() {
	    super.onStop();
	    EasyTracker.getInstance(this).activityStop(this);  // Add this method.
	}
	
	public void onResume() {
		super.onResume();
		
		if(mPref.getBoolean("complete_logout", false)) {
			Intent intent = new Intent(this, MTBLoginPage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
        	finish();
		}
	}
	
	private void clearCache() {
		File cacheFileDir = getCacheDir();
	
        cacheFileDir.delete();
        
        File dataFileDir = getFilesDir();
        
        dataFileDir.delete();
	}
	
	class logoutFromApp extends AsyncTask<Void, Void, Boolean> {
		ProgressDialog dialog;
		
		@Override
		public void onPreExecute() {
			dialog = ProgressDialog.show(MTBSettings.this, "Logging out...", "Please wait...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... params) {
			/*
			File dir = new File(Constants.MTB_CACHES);
				
			if (dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					new File(dir, children[i]).delete();
				}
			}
			*/
			
			MTBUploadHandler upload = new MTBUploadHandler(MTBSettings.this);
			return upload.logout();
		}
		
		@Override
		public void onPostExecute(Boolean returnValue) {
			dialog.dismiss();
			
			if(returnValue) {
				
				// unregister Alarm
   				unregisterAlarm();
   				
   				// clear cache
   				clearCache();
   				
   				// clear all sharedpreferences values.
   				SharedPreferences.Editor editor = mPref.edit();
   				editor.clear();
   				editor.putBoolean("complete_logout", true);
				editor.commit();
				
				Log.i("K", "Logging out from the app");
				
				//Intent intent = new Intent(MTBSettings.this, MTBLoginPage.class);
				//intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	//intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
	        	//startActivity(intent);
		    	//finish();
				
				finish();
				
			}
			else {
				Toast.makeText(MTBSettings.this, "Failed to logout. Please try again", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class DeleteCache extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog;
		
		@Override
		public void onPreExecute() {
			dialog = ProgressDialog.show(MTBSettings.this, "Deleting...", "Please wait...", true);
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			File dir = new File(Constants.MTB_CACHES);
				
			if (dir.isDirectory()) {
				String[] children = dir.list();
				for (int i = 0; i < children.length; i++) {
					new File(dir, children[i]).delete();
				}
			}
			
			return null;
		}
		
		@Override
		public void onPostExecute(Void unused) {
			dialog.dismiss();
		}
		
	}
	
	// unregister alarm when log out
	public void unregisterAlarm() {
		Log.i("K", "unregister alarm");
		
		Intent intent = new Intent();
		PendingIntent sender = PendingIntent.getBroadcast(this, 0, intent, 0);
		AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		
		manager.cancel(sender);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == 1){
			
			Log.i("K", "Logging out from the app");
			
			Intent intent = new Intent(MTBSettings.this, MTBLoginPage.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
	    	finish();
		}
		else if(resultCode == RESULT_OK && requestCode == 2) {
			
			switch(data.getExtras().getInt("flag")) {
			case 0:
				finish();
				startActivity(new Intent(MTBSettings.this, MTBMessagePage.class));
				break;
			case 1:
				finish();
				startActivity(new Intent(MTBSettings.this, MTBOfferPage.class));
				break;
			case 2:
				finish();
				startActivity(new Intent(MTBSettings.this, MTBRequestPage.class));
				break;
			case 3:
				finish();
				startActivity(new Intent(MTBSettings.this, MTBSearchMainPage.class));
				break;
			case 4:
				finish();
				startActivity(new Intent(MTBSettings.this, MTBGroupMainPage.class));
				break;	
			case 5:
				// oneself
				break;
			case 6:
				new AlertDialog.Builder(MTBSettings.this)
				.setTitle("Exit")
				.setIcon(R.drawable.hourworld_icon_30_30)
				.setMessage("Exit Timebank?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// out of the application
						finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// nothing to do
					}
				})
				.show();
			}
		}
	}
	
	class SettingItem {
		public String header;
		public String name;
	}
	
	class TBSettingsAdapter extends BaseAdapter {
		
		private LayoutInflater mInflater;
		private int mLayout;
		
		public TBSettingsAdapter(Context context, int layout) {
			mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			mLayout = layout;
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArr.size();
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if(convertView == null)
				convertView = mInflater.inflate(mLayout, parent, false);
			
			TextView name = (TextView)convertView.findViewById(R.id.name);
			
			name.setText(mArr.get(position).name);
				
			return convertView;
		}
	}

	/*
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			// do nothing
		}
		else if(keyCode == KeyEvent.KEYCODE_MENU) {
			int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
			SlideoutActivity.prepare(this, R.id.inner_content, width);
			Intent intent = new Intent(MTBSettings.this, MTBSideBarActivity.class);
			intent.putExtra("from", Constants.FROM_SETTINGS);
			
			startActivityForResult(intent, 2);
			overridePendingTransition(0, 0);
		}
		
		return false;
	}*/
	
}
