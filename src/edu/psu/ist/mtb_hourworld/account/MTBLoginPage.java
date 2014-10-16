package edu.psu.ist.mtb_hourworld.account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import edu.psu.ist.mtb_hourworld.MTBAboutPage;
import edu.psu.ist.mtb_hourworld.MTBMainMenuPage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.utilities.MTBStringHash;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MTBLoginPage extends Activity {

	private ProgressDialog mDialog = null;
	private EditText vEmail;
	private EditText vPassword;
	private String mEmail;
	private String mPassword;
	private String mChallenge;
	private String mMixedMD5;
	private boolean mLoginValid = false;
	
	private Button mMTLoginBtn;
	private Button mRegisterBtn;
	private Button mAboutBtn;
	
	private SharedPreferences mPref;
	
	private String android_id;
	private int mPrevActivity;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mtb_login);
		
		Intent gIntent = getIntent();
		mPrevActivity = gIntent.getExtras().getInt("prev_activity");
		
		android_id = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);
		
		mMTLoginBtn = (Button) findViewById(R.id.login_button);
		mRegisterBtn = (Button)findViewById(R.id.register_button);
		mAboutBtn = (Button)findViewById(R.id.about_button);
		
		vEmail = (EditText)findViewById(R.id.email);
		vPassword = (EditText)findViewById(R.id.password);

		//Define Preferences to store username and password
		mPref = PreferenceManager.getDefaultSharedPreferences(this);
		
		ActionBar actionBar = getActionBar();
		// hide the action bar
		actionBar.hide();

		mMTLoginBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
	
				mEmail = vEmail.getText().toString();
				mPassword = vPassword.getText().toString();
				
				if(mEmail.equals("") || mPassword.equals("")){
					Toast.makeText(MTBLoginPage.this, "Username and/or password not recognized. Please check and try again.", Toast.LENGTH_SHORT).show();	
				}else{
					
					
					MTBStringHash mHash = new MTBStringHash();
					
					mChallenge = mHash.createChallenge();
					Log.i("K", "mChallenge : " + mChallenge);
					
					String passwordMD5 = mHash.md5(mPassword);
					Log.i("K", "passwordMD5 : " + passwordMD5);
					
					mMixedMD5 = mHash.md5(mChallenge + passwordMD5);
					Log.i("K", "mMixedMD5 : " + mMixedMD5);
					
					new AsyncLogin().execute();
				}
			}
		});

		mRegisterBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(MTBLoginPage.this, MTBRegisterPage.class);
				//intent.putExtra("prev_activity", mPrevActivity);
				startActivity(intent);
			}
		});
		
		mAboutBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(MTBLoginPage.this, MTBAboutPage.class));
			}
		});
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_sm, menu);
 
        return super.onCreateOptionsMenu(menu);
    }

	public void onResume() {
		super.onResume();
		
		if(mPref.getBoolean("login_page_disappear", false)) {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		super.onKeyDown(keyCode, event);
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	        finish();
	        return true;
	    }
	    return false;
	}
	
	/**
	 * Async class for the login process
	 */
	class AsyncLogin extends AsyncTask<Void, Integer, Void> {
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBLoginPage.this, "Loading...", "Logging into hOurworld...", true);
		}
		@Override
		protected Void doInBackground(Void... arg0) {
			
			HttpClient httpClient = new DefaultHttpClient();  	
			//String url = new String(Constants.REGISTER_LINK);
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("username", new StringBody(mEmail));
		    	entity.addPart("challenge", new StringBody(mChallenge));
		    	entity.addPart("response", new StringBody(mMixedMD5));
	        	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		   
			httpPost.setEntity(entity);

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
					
					Log.i("K", "login result: " + result);
				
					JSONObject jsonobj = new JSONObject(result);
					
					if(!jsonobj.getBoolean("success")){
						mLoginValid = false;
					}
					else {
						mLoginValid = true;
						
						// update the sharedpreference values
						SharedPreferences.Editor editor = mPref.edit();
						editor.putString("username", mEmail.trim());
						editor.putString("password", mPassword.trim());
						editor.putBoolean("loggedin", true);
			    		editor.putBoolean("guest_mode", false);
			    		editor.putBoolean("facebook", false);
			    		
			    		JSONObject resultObj = jsonobj.getJSONObject("results");
			    		// 05/14/13 (T&T is using access_token)
			    		editor.putString("access_token", resultObj.getString("access_token"));
			    		editor.putInt("EID", resultObj.getInt("EID")); //1001
			    		editor.putInt("memID", resultObj.getInt("memID")); //2596
			    		editor.putInt("orgID", resultObj.getInt("orgID")); //
			    		// f2q7r5fvli95i0mlsdke808342
			    		
			    		editor.putString("orgName", resultObj.getString("orgName"));
			    		editor.putString("orgMotto", resultObj.getString("orgMotto"));
			    		editor.putString("orgCSZ", resultObj.getString("orgCSZ"));
			    		editor.putString("orgPhone", resultObj.getString("orgPhone"));
			    		editor.putString("orgRefs", resultObj.getString("orgRefs"));
			    		editor.putString("orgImg", Constants.HOURWORLD + resultObj.getString("orgImg"));
			    		
			    		Log.i("K", "Login success. Received the following values");
			    		Log.i("K", "accessToken : " + resultObj.getString("access_token"));
			    		Log.i("K", "EID : " + resultObj.getInt("EID"));
			    		Log.i("K", "memID : " + resultObj.getInt("memID"));
			    		Log.i("K", "orgID : " + resultObj.getInt("orgID"));
			    		Log.i("K", "orgMotto : " + resultObj.getString("orgMotto"));
			    		Log.i("K", "orgCSZ : " + resultObj.getString("orgCSZ"));
			    		Log.i("K", "orgPhone : " + resultObj.getString("orgPhone"));
			    		Log.i("K", "orgRefs : " + resultObj.getString("orgRefs"));
			    		Log.i("K", "orgImg : " + Constants.HOURWORLD + resultObj.getString("orgImg"));
			    		
			    		editor.putBoolean("complete_logout", false);
			    		editor.putBoolean("notification_on", true);
			    		
			    		editor.putString("android_id", android_id);
			    		editor.commit();
			    		
			    		//registerClient();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
		
		protected void onPostExecute(Void unused) {
			
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(!mLoginValid) {
				Toast.makeText(MTBLoginPage.this, "Incorrect Username/Password. Please try again.", Toast.LENGTH_SHORT).show();
			}
			else {
				Intent intent = new Intent(MTBLoginPage.this, MTBMainMenuPage.class);
            	intent.putExtra("terminate", false);
            	startActivity(intent);
            	finish();
			}
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
