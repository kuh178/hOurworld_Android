package edu.psu.ist.mtb_hourworld.profile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.profile.MTBProfileEditContactPage.contactItem;
import edu.psu.ist.mtb_hourworld.profile.MTBProfileEditContactPage.downloadContact;
import edu.psu.ist.mtb_hourworld.utilities.MTBUploadHandler;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MTBProfileEditAddressPage extends Activity {

	private SharedPreferences mPref;
	
	private TextView mLoadingText;
	private ProgressBar mLoadingBar;
	
	private EditText vLastNameTxt;
	private EditText vFirstNameTxt;
	private EditText vStreetAddressTxt;
	private EditText vCityTxt;
	private EditText vStateTxt;
	private EditText vZipcodeTxt;
	private EditText vBirthdateTxt;
	private EditText vWorkplaceTxt;
	
	private String mLastName;
	private String mFirstName;
	private String mStreetAddress;
	private String mCity;
	private String mState;
	private String mZipcode;
	private String mBirthDate;
	private String mWorkplace;
	
	private Button vSubmitBtn;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_profile_edit_address);
	
	    vLastNameTxt = (EditText)findViewById(R.id.last_name);
	    vFirstNameTxt = (EditText)findViewById(R.id.first_name);
	    vStreetAddressTxt = (EditText)findViewById(R.id.address);
	    vCityTxt = (EditText)findViewById(R.id.city);
	    vStateTxt = (EditText)findViewById(R.id.state);
	    vZipcodeTxt = (EditText)findViewById(R.id.zip);
	    vBirthdateTxt = (EditText)findViewById(R.id.birth_date);
	    vWorkplaceTxt = (EditText)findViewById(R.id.work_place);
	    
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    vSubmitBtn = (Button)findViewById(R.id.update_btn);
	    
	    vSubmitBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mLastName = vLastNameTxt.getText().toString().trim();
				mFirstName = vFirstNameTxt.getText().toString().trim();
				mStreetAddress = vStreetAddressTxt.getText().toString().trim();
				mCity = vCityTxt.getText().toString().trim();
				mState = vStateTxt.getText().toString().trim();
				mZipcode = vZipcodeTxt.getText().toString().trim();
				mBirthDate = vBirthdateTxt.getText().toString().trim();
				mWorkplace = vWorkplaceTxt.getText().toString().trim();
				
				if(mLastName.length() == 0 || mFirstName.length() == 0) {
					Toast.makeText(MTBProfileEditAddressPage.this, "Please check your name", Toast.LENGTH_SHORT).show();
				}
				else {
					new updateAddress().execute();
				}
			}
		});
	    
	    setTitle("Edit address");
	    getActionBar().setIcon(R.drawable.profile);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	 
	    new downloadAddress().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	

	class updateAddress extends AsyncTask<Void, Void, Boolean> {

		private ProgressDialog mDialog;
		
		@Override
	    protected void onPreExecute() {

			mDialog = new ProgressDialog(MTBProfileEditAddressPage.this);
			mDialog.setTitle("Updating address");
			mDialog.setMessage("Please waiting...");
			mDialog.setCancelable(true);
			mDialog.setOnCancelListener(new OnCancelListener() {
				@Override
				public void onCancel(DialogInterface arg0) {
					mDialog.dismiss();
					Toast.makeText(MTBProfileEditAddressPage.this, "Not posted", Toast.LENGTH_SHORT).show();
				}
	    	});
			mDialog.show();
	    }
		
		@Override
		protected Boolean doInBackground(Void... params) {
			MTBUploadHandler upload = new MTBUploadHandler(MTBProfileEditAddressPage.this);
			return upload.updateAddress(mLastName, mFirstName, mStreetAddress, mCity, mState, mZipcode, mBirthDate, mWorkplace);
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
	  		if(mDialog.isShowing()) {
	  			mDialog.dismiss();
	  		}
	  		
	  		Intent intent = getIntent();
	  		
	  		if(returnValue) {
	  			Toast.makeText(MTBProfileEditAddressPage.this, "Upadted successfully", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", true);
	  		}
	  		else {
	  			Toast.makeText(MTBProfileEditAddressPage.this, "Error while updating. Please try again...", Toast.LENGTH_SHORT).show();
				intent.putExtra("refresh", false);
	  		}
	  		
	  		setResult(RESULT_OK, intent);
			finish();
		}
	}
	
	
	
	/*

	 */
	class downloadAddress extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("EditAddress,EDIT")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
		    	Log.i("K", "requestType: " + "EditAddress,EDIT");
		    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
		    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
		    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
		    	
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
					
					Log.i("K", "results: " + result);
					
					JSONObject jObj = new JSONObject(result);
				
					if(!jObj.getBoolean("success")){
						// show the failure message
						return false;
					}
					else {
						JSONObject jObjDetail = jObj.getJSONArray("results").getJSONObject(0);
						
						mLastName = jObjDetail.getString("Lastname");
						mFirstName = jObjDetail.getString("Firstname");
						mStreetAddress = jObjDetail.getString("Street");
						mCity = jObjDetail.getString("City");
						mState = jObjDetail.getString("State");
						mZipcode = jObjDetail.getString("Zip");
						mBirthDate = jObjDetail.getString("BirthDate");
						mWorkplace = jObjDetail.getString("WorkPlace");
						
						return true;
					}
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
	
			return false;
		}
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			
			if(returnValue) {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);

				vLastNameTxt.setText(mLastName);
				vFirstNameTxt.setText(mFirstName);
				vStreetAddressTxt.setText(mStreetAddress);
				vCityTxt.setText(mCity);
				vStateTxt.setText(mState);
				vZipcodeTxt.setText(mZipcode);
				vBirthdateTxt.setText(mBirthDate);
				vWorkplaceTxt.setText(mWorkplace);
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setText("No data");
			}
		}
	}
}
