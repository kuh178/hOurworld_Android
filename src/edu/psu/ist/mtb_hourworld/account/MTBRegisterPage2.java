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

import edu.psu.ist.mtb_hourworld.MTBOfferPage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.tasks.MTBAddRequestOfferPage3;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MTBRegisterPage2 extends Activity {

	private String mFirstname = "";
	private String mLastname = "";
	private String mStreet = "";
	private String mCity = "";
	private String mState = "";
	private String mZip = "";
	private String mMonth = "";
	private String mDay = "";
	private String mYear = "";
	private String mBio = "";
	private String mEmail = "";
	private String mPassword = "";
	private String mPassword2 = "";
	private String mPhone = "";
	
	
	private EditText vFirstname;
	private EditText vLastname;
	private EditText vStreet;
	private EditText vCity;
	private EditText vState;
	private EditText vZip;
	private EditText vMonth;
	private EditText vDay;
	private EditText vYear;
	private EditText vBio;
	private EditText vEmail;
	private EditText vPassword;
	private EditText vPassword2;
	private EditText vPhone;
	
	private EditText vRefName1;
	private EditText vRefEmail1;
	private EditText vRefPhone1;
	private EditText vRefName2;
	private EditText vRefEmail2;
	private EditText vRefPhone2;
	
	
	private Button vRegisterBtn;
	
	private int mExchangeID;
	private String mIsCollectingRef = "";
	private String mRefName1 = ""; 
	private String mRefEmail1 = ""; 
	private String mRefPhone1 = ""; 
	private String mRefName2 = "";
	private String mRefEmail2 = ""; 
	private String mRefPhone2 = "";
	
	private LinearLayout vRefLayout;
	
	private Button vRefBtn;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    
	    setContentView(R.layout.mtb_register_page2);
	    
	    Intent gIntent = getIntent();
	    mExchangeID = gIntent.getExtras().getInt("exchangeID");
	    
	    Log.i("K", "mExchangeID : " + mExchangeID);
	   
	    vFirstname = (EditText)findViewById(R.id.firstname);
		vLastname = (EditText)findViewById(R.id.lastname);
		vStreet = (EditText)findViewById(R.id.street);
		vCity = (EditText)findViewById(R.id.city);
		vState = (EditText)findViewById(R.id.state);
		vZip = (EditText)findViewById(R.id.zip);
		vMonth = (EditText)findViewById(R.id.month);
		vDay = (EditText)findViewById(R.id.day);
		vYear = (EditText)findViewById(R.id.year);
		vBio = (EditText)findViewById(R.id.bio);
		vEmail = (EditText)findViewById(R.id.email);
		vPassword = (EditText)findViewById(R.id.password);
		vPassword2 = (EditText)findViewById(R.id.password_again);
		vPhone = (EditText)findViewById(R.id.phone);
	    vRegisterBtn = (Button)findViewById(R.id.register_btn);
	    
	    vRefName1 = (EditText)findViewById(R.id.refname1);
	    vRefEmail1 = (EditText)findViewById(R.id.refemail1);
	    vRefPhone1 = (EditText)findViewById(R.id.refphone1);
	    vRefName2 = (EditText)findViewById(R.id.refname2);
	    vRefEmail2 = (EditText)findViewById(R.id.refemail2);
	    vRefPhone2 = (EditText)findViewById(R.id.refphone2);
	    
	    vRefLayout = (LinearLayout)findViewById(R.id.reference_layout);
	    
	    vRefBtn = (Button)findViewById(R.id.referenceBtn);
	    
	    setTitle("(Registration) Fill out the form");
	    //getActionBar().setIcon(R.drawable.news);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    vRegisterBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mFirstname = vFirstname.getText().toString().trim();
				mLastname = vLastname.getText().toString().trim();
				mStreet = vStreet.getText().toString().trim();
				mCity = vCity.getText().toString().trim();
				mState = vState.getText().toString().trim();
				mZip = vZip.getText().toString().trim();
				mMonth = vMonth.getText().toString().trim();
				mDay = vDay.getText().toString().trim();
				mYear = vYear.getText().toString().trim();
				mBio = vBio.getText().toString().trim();
				mEmail = vEmail.getText().toString().trim();
				mPassword = vPassword.getText().toString().trim();
				mPassword2 = vPassword2.getText().toString().trim();
				mPhone = vPhone.getText().toString().trim();
				
				if(mFirstname.equals("") ||
						mFirstname.equals("") ||
						mLastname.equals("") ||
						mStreet.equals("") ||
						mCity.equals("") ||
						mState.equals("") ||
						mZip.equals("") ||
						mMonth.equals("") ||
						mDay.equals("") ||
						mYear.equals("") ||
						mBio.equals("") ||
						mEmail.equals("") ||
						mPassword.equals("") ||
						mPassword2.equals("")) {
					Toast.makeText(MTBRegisterPage2.this, "Check all fields", Toast.LENGTH_SHORT).show();
					
				}
				else if(!mPassword.equals(mPassword2)) {
					Toast.makeText(MTBRegisterPage2.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
				}
				else {
					// checking references
					if(mIsCollectingRef.equals("T")) {
						
						mRefName1 = vRefName1.getText().toString();
						mRefPhone1 = vRefPhone1.getText().toString();
						mRefEmail1 = vRefEmail1.getText().toString();
						mRefName2 = vRefName2.getText().toString();
						mRefPhone2 = vRefPhone2.getText().toString();
						mRefEmail2 = vRefEmail2.getText().toString();
						
						// need at least one reference
						if(((!mRefName1.equals("") && !mRefPhone1.equals("")) ||
							(!mRefName1.equals("") && !mRefEmail1.equals(""))) 
							&&
							(!mRefName2.equals("") && !mRefPhone2.equals("")) ||
							(!mRefName2.equals("") && !mRefEmail2.equals(""))
							) {
							// register
							new uploadRegister().execute();
						}
						
						
					}
					else {
						// register
						new uploadRegister().execute();
					}
				}
				
			}
		});
	    
	    vRefBtn.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showDialog();
			}
		});
	    
	    // get reference
	    new checkReference().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	private void showDialog() {
		
		final LinearLayout linear = (LinearLayout) View.inflate(MTBRegisterPage2.this, R.layout.mtb_whatisreference, null);
		
		new AlertDialog.Builder(MTBRegisterPage2.this)
		.setTitle("New member reference")
		.setView(linear)
		.setNegativeButton("Close", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
			}
		})
		.show();
	}
	
	
	class checkReference extends AsyncTask<Void, Integer, Boolean>{

		private ProgressDialog mDialog;
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBRegisterPage2.this, "Loading...", "Please wait...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.JOIN);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("getRefs," + mExchangeID)); // specify a type of this request
		    	
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
					
					// {"success":true,"results":"F"}
					JSONObject jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						mIsCollectingRef = jObj.getString("results");
					}
					else {
						
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
		
		@Override
	  	protected void onPostExecute(Boolean returnValue) {
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(mIsCollectingRef.equals("T")) {
				vRefLayout.setVisibility(View.VISIBLE);
			}
			else {
				vRefLayout.setVisibility(View.GONE);
			}
		}
	}
	
	
	class uploadRegister extends AsyncTask<Void, Integer, Boolean>{

		private ProgressDialog mDialog;
		
		protected void onPreExecute() {
			mDialog = ProgressDialog.show(MTBRegisterPage2.this, "Loading...", "Please wait...", true);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.JOIN);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("AddMbr," + mExchangeID)); // specify a type of this request
		    	entity.addPart("getRefs", new StringBody(mIsCollectingRef)); 
		    	entity.addPart("Firstname", new StringBody(mFirstname)); 
		    	entity.addPart("Lastname", new StringBody(mLastname));
		    	entity.addPart("Street", new StringBody(mStreet));
		    	entity.addPart("City", new StringBody(mCity)); 
		    	entity.addPart("State", new StringBody(mState));
		    	entity.addPart("Zip", new StringBody(mZip)); 
		    	entity.addPart("BDate", new StringBody(mMonth + "/" + mDay + "/" + mYear)); 
		    	entity.addPart("Bio", new StringBody(mBio));
		    	entity.addPart("Email", new StringBody(mEmail)); 
		    	entity.addPart("Pwd", new StringBody(mPassword));
		    	entity.addPart("Phone", new StringBody(mPhone));
		    	
		    	Log.i("K", "AddMbr," + mExchangeID);
		    	Log.i("K", mIsCollectingRef);
		    	Log.i("K", mFirstname);
		    	Log.i("K", mLastname);
		    	Log.i("K", mStreet);
		    	Log.i("K", mCity);
		    	Log.i("K", mState);
		    	Log.i("K", mZip);
		    	Log.i("K", mMonth + "/" + mDay + "/" + mYear);
		    	Log.i("K", mBio);
		    	Log.i("K", mEmail);
		    	Log.i("K", mPassword);
		    	Log.i("K", mPhone);
		    	
		    	if(mIsCollectingRef.equals("T")) {
		    		entity.addPart("Ref1Name", new StringBody(mRefName1)); 
			    	entity.addPart("Ref1Email", new StringBody(mRefEmail1)); 
			    	entity.addPart("Ref1Phone", new StringBody(mRefPhone1)); 
			    	entity.addPart("Ref2Name", new StringBody(mRefName2));
			    	entity.addPart("Ref2Email", new StringBody(mRefEmail2)); 
			    	entity.addPart("Ref2Phone", new StringBody(mRefPhone2)); 
		    	}
		    
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
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
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
			if(mDialog.isShowing()) {
				mDialog.dismiss();
			}
			
			if(returnValue) {
				Toast.makeText(MTBRegisterPage2.this, "Your Application Has Been Sent! You will receive a welcome email with next steps once your account is setup. This may take a couple days.", Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(MTBRegisterPage2.this, MTBLoginPage.class);
				intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
			else {
				
			}
		}
	}
}
