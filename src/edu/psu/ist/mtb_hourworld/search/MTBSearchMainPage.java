package edu.psu.ist.mtb_hourworld.search;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.analytics.tracking.android.EasyTracker;

import edu.psu.ist.mtb_hourworld.MTBMessagePage;
import edu.psu.ist.mtb_hourworld.MTBOfferPage;
import edu.psu.ist.mtb_hourworld.MTBRequestPage;
import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.adapter.MTBSearchAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.group.MTBGroupMainPage;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.settings.MTBSettings;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class MTBSearchMainPage extends Activity {

	private SharedPreferences mPref;
	private ProgressBar mLoadingBar;
    private TextView mLoadingText;
    private ListView mListView;
    private Button menuBtn;
	private TextView mMenuText;
	
	private EditText mSearchText;
	private Button mSearchSubmit;
	
	private String mSearchTerm;
	
	private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_search_list_view);
	    
	    mLoadingBar = (ProgressBar)findViewById(R.id.spinner);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mListView = (ListView)findViewById(R.id.listview);
	    menuBtn = (Button)findViewById(R.id.menu_btn);
	    
	    // updated Jan. 22
        menuBtn.setEnabled(false);
        menuBtn.setVisibility(View.GONE);
	    
        mMenuText = (TextView)findViewById(R.id.menu_text);
        mSearchText = (EditText)findViewById(R.id.search_term);
        mSearchSubmit = (Button)findViewById(R.id.search_submit);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    //mMenuText.setText(" Search");
	    
	    setTitle("Search");
	    getActionBar().setIcon(R.drawable.search);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);

	    mSearchSubmit.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				mSearchTerm = mSearchText.getText().toString().trim();

				// check the string
				if(!mSearchTerm.equals("")) {
					new startSearch().execute();
				}
				else {
					Toast.makeText(MTBSearchMainPage.this, "Check a search term", Toast.LENGTH_SHORT).show();
				}
			}
		});
	    
	    
	    mSearchText.setFocusableInTouchMode(true);
	    mSearchText.requestFocus();
	    mSearchText.setOnKeyListener(new View.OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {

				mSearchTerm = mSearchText.getText().toString().trim();
				
				if(!mSearchTerm.equals("")) {
					// If the event is a key-down event on the "enter" button
			        if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
			        	// Perform action on key press
			        	new startSearch().execute();
							
			        	return true;
			        }
				}
				
				return false;
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

	
	/*
	 * Search term all
	 */
	class startSearch extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected void onPreExecute() {
			mLoadingText.setText("Loading results...");
			mLoadingBar.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			String url = new String(Constants.AUTHENTICATE);
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();

		    try {
		    	entity.addPart("requestType", new StringBody("Search,All")); // specify a type of this request
		    	entity.addPart("SearchChars", new StringBody(mSearchTerm));
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
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
						// show the failure message
					}
					else {
						mArr.clear();
						
						JSONArray jAry = jObj.getJSONArray("results");
						
						// return false if there's no value
						if(jAry.length() == 0) {
							return false;
						}
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							// pass this KB mod
							if(jItemObj.getString("requestMod").equals("KB")) {
								continue;
							}
							
							MTBTaskItems item = new MTBTaskItems();
							item.setRequestType(jItemObj.getString("requestType"));
							item.setRequestMode(jItemObj.getString("requestMod"));
							item.setIcon("http://www.hourworld.org/" + jItemObj.getString("icon"));
							item.setMembersCount(jItemObj.getInt("Members"));
							//item.setCategoryCount(jItemObj.getInt("Categories"));
							item.setLinkText(jItemObj.getString("linkText"));
							item.setSearchChar(jItemObj.getString("SearchChars"));
							
							
							
							mArr.add(item);
						}
						
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
				mListView.setVisibility(View.VISIBLE);
				
				
				MTBSearchAdapter adapter = new MTBSearchAdapter(MTBSearchMainPage.this, R.layout.mtb_search_list_item, mArr);
				
				mListView.setAdapter(adapter);
				
				mListView.setOnItemClickListener(new OnItemClickListener(){

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
						
						MTBTaskItems item = mArr.get(position);

						if(item.getRequestMode().equals("Pro")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchProvider.class);
							intent.putExtra("type", "Pro");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("Rcv")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchProvider.class);
							intent.putExtra("type", "Rcv");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);						
						}
						else if(item.getRequestMode().equals("Bio")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchBio.class);
							intent.putExtra("type", "Bio");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("Name")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchName.class);
							intent.putExtra("type", "Name");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("Msg")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchMessage.class);
							intent.putExtra("type", "Msg");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
						else if(item.getRequestMode().equals("KB")) {
							Intent intent = new Intent(MTBSearchMainPage.this, MTBSearchKB.class);
							intent.putExtra("type", "KB");
							intent.putExtra("term", mSearchTerm);
							startActivity(intent);
						}
					}
					
				});
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				//mLoadingText.setVisibility(View.GONE);
				mLoadingText.setText("Nothing returned");
			}
		}
	}
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == 1){
			Intent intent = new Intent(MTBSearchMainPage.this, MTBLoginPage.class);
        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
        	startActivity(intent);
	    	finish();
		}
		else if(resultCode == RESULT_OK && requestCode == 2) {
			
			switch(data.getExtras().getInt("flag")) {
			case 0:
				finish();
				startActivity(new Intent(MTBSearchMainPage.this, MTBMessagePage.class));
				break;
			case 1:
				finish();
				startActivity(new Intent(MTBSearchMainPage.this, MTBOfferPage.class));
				break;
			case 2:
				finish();
				startActivity(new Intent(MTBSearchMainPage.this, MTBRequestPage.class));
				break;
			case 3:
				break;
			case 4:
				finish();
				startActivity(new Intent(MTBSearchMainPage.this, MTBGroupMainPage.class));
				break;
			case 5:
				finish();
				startActivity(new Intent(MTBSearchMainPage.this, MTBSettings.class));
				break;
			case 6:
				new AlertDialog.Builder(MTBSearchMainPage.this)
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
	
	
	// or when user press back button
	// when you hold the button for 3 sec, the app will be exited
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			
			return true;
		}
		/*
		else if(keyCode == KeyEvent.KEYCODE_MENU) {
			int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, getResources().getDisplayMetrics());
			SlideoutActivity.prepare(MTBSearchMainPage.this, R.id.inner_content, width);
			
			Intent intent = new Intent(MTBSearchMainPage.this, MTBSideBarActivity.class);
			intent.putExtra("from", Constants.FROM_SEARCH);
			
			startActivityForResult(intent, 2);
			overridePendingTransition(0, 0);
		}
		*/
		
		return false;
	}
}
