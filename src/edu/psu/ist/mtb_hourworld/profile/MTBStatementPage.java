package edu.psu.ist.mtb_hourworld.profile;

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

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.adapter.MTBStatementAdapter;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class MTBStatementPage extends Activity {

	private ProgressBar vLoadingBar;
	private TextView vLoadingText;
	private ListView vListView;
	
	private ArrayList<StatementItem> mArr = new ArrayList<StatementItem>();
	
	private SharedPreferences mPref;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_statement);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    vLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    vLoadingText = (TextView)findViewById(R.id.loading_text);
	    vListView = (ListView)findViewById(R.id.listview);
	    
	    setTitle("Statement");
	    getActionBar().setIcon(R.drawable.profile);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    new downloadStatement().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_none, menu);
 
        return super.onCreateOptionsMenu(menu);
    }
	
	public class StatementItem {
		public int transID;
		public String transDate;
		public String transOrigin;
		public String transHappy;
		public String transRefer;
		public String transNote;
		public String provider;
		public String receiver;
		public String TDs;
		public String svcCat;
		public String service;
		public int svcCatID;
		public int svcID;
		private int providerID;
		private int receiverID;
	}
	
	/*
	 * Download Timebank Information from the server
	 */
	class downloadStatement extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();  	
			String url = new String(Constants.AUTHENTICATE);
		    
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();
		    
		    try {
		    	entity.addPart("requestType", new StringBody("Statement,365")); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
		    	Log.i("K", "requestType:Statement,0");
		    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
		    	Log.i("K", "EID: " + Integer.toString(mPref.getInt("EID", 0)));
		    	Log.i("K", "memID: " + Integer.toString(mPref.getInt("memID", 0)));
		    	
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
						JSONArray jAry = jObj.getJSONArray("results");
						
						for(int i = 0; i < jAry.length(); i++){
							
							JSONObject jItemObj = jAry.getJSONObject(i);
							
							StatementItem item = new StatementItem();
							item.transID = jItemObj.getInt("transID");
							item.transDate = jItemObj.getString("transDate");
							item.transHappy = jItemObj.getString("transHappy");
							item.transRefer = jItemObj.getString("transRefer");
							item.transNote = jItemObj.getString("transNote");
							item.provider = jItemObj.getString("ProName");
							item.receiver = jItemObj.getString("RcvName");
							item.TDs = jItemObj.getString("TDs");
							item.svcCat = jItemObj.getString("SvcCat");
							item.service = jItemObj.getString("Service");
							item.svcCatID = jItemObj.getInt("SvcCatID");
							item.svcID = jItemObj.getInt("SvcID");
							item.providerID = jItemObj.getInt("Provider");
							item.receiverID = jItemObj.getInt("Receiver");
						
							mArr.add(item);
						}
			
						if(mArr.size() == 0) {
							return false;
						}
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
			if(returnValue) {
				vLoadingBar.setVisibility(View.GONE);
				vLoadingText.setVisibility(View.GONE);
				vListView.setVisibility(View.VISIBLE);

				loadListView();
			}
			else {
				vLoadingBar.setVisibility(View.GONE);
				vLoadingText.setText("No information.");
				vListView.setVisibility(View.GONE);
			}
		}
	}
	
	private void loadListView() {
		
		Log.i("K", "mArr size : " + mArr.size());
		
		MTBStatementAdapter adapter = new MTBStatementAdapter(MTBStatementPage.this, R.layout.mtb_statement_view_item, mArr);
		vListView.setAdapter(adapter);
        
		/*
		vListView.setTag(mArr);
		vListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> view, View v, int position, long arg3) {
				
				//StatementItem item = mArr.get(position);
				//startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
			}
        });
        */
	}
}
