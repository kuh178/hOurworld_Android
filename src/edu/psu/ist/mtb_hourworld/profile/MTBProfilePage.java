package edu.psu.ist.mtb_hourworld.profile;

import java.io.BufferedReader;
import java.io.File;
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

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.account.MTBLoginPage;
import edu.psu.ist.mtb_hourworld.constants.Constants;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.tasks.MTBReportHoursPage;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskDetailPage;
import edu.psu.ist.mtb_hourworld.utilities.MTBImageLoader;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MTBProfilePage extends Activity {

	private ImageView userImageView;
	private TextView userNameView;
	private TextView activityView;
	private TextView contactView;
	private TextView addressView;
	private TextView bioView;
	private TextView balanceView;

	private Button messageBtn;
	private Button vReportHourBtn;
	
	private ImageView vBadge1;
	private ImageView vBadge2;
	private ImageView vBadge3;
	private ImageView vBadge4;
	private ImageView vBadge5;
	private ImageView vBadge6;
	
	private SharedPreferences mPref;

	private int mUserID;
	
	private MTBImageLoader imageLoader;
	
	private String mContact = "";
	private String mUsername;
	private String mProfileImage;
	private String mCity;
	private String mBio;
	private String mProvidedHours;
	private String mReceivedHours;
	
	private String mTotalServicesLink;
	private String mTotalReceivesLink;
	private String mTotalExcHoursLink;
	private String mTotalRefsLink;
	//private String mTotalGroupsLink;
	private String mDiversityLink;
	//private String mPageVisitsLink;
	private String mConsecMoLink;
	//private String mOrientationLink;
	//private String mProfileImageDoneLink;
	//private String mAdvTrainingLink;
	
	private String mTotalServicesTitle;
	private String mTotalReceivesTitle;
	private String mTotalExcHoursTitle;
	private String mTotalRefsTitle;
	//private String mTotalGroupsTitle;
	private String mDiversityTitle;
	//private String mPageVisitsTitle;
	private String mConsecMoTitle;
	//private String mOrientationTitle;
	//private String mProfileImageDoneTitle;
	//private String mAdvTrainingTitle;

	private double mBalance;
	
	private int mTotalTrans;
	private int mTotalMembers;
	private int mTotalReceived;
	private int mSatisfaction;
	
	private TableLayout groupSuperView;
	private TableLayout offerSuperView;
	private TableLayout requestSuperView;
	
	private ProgressBar mLoadingBar;
	private TextView mLoadingText;
	private ScrollView mScrollView;
	private LinearLayout mFooter;
	private LinearLayout vBalanceView;
	
	private Button vContactEditBtn;
	private Button vAddressEditBtn;
	private Button vBioEditBtn;
	private Button vGroupEditBtn;
	private Button vStatementBtn;
	
	private RadioGroup vShareGroup;
	private RadioButton	vShareBtn;
	private String mShare;
	
	private LayoutInflater vi;
	private ArrayList<groupItem> mGroupArr = new ArrayList<groupItem>();
	private ArrayList<MTBTaskItems> mOfferArr = new ArrayList<MTBTaskItems>();
	private ArrayList<MTBTaskItems> mRequestArr = new ArrayList<MTBTaskItems>();
	
	private boolean mProfileEdit = false;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	
	    setContentView(R.layout.mtb_profile);

	    Intent gIntent = getIntent();
	    mUserID = gIntent.getExtras().getInt("userID", 0);
	    
	    mPref = PreferenceManager.getDefaultSharedPreferences(this);
	    
	    vi = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    
	    imageLoader = new MTBImageLoader(this);
	    
	    // three superviews
	    offerSuperView = (TableLayout)findViewById(R.id.offer_super_view);
	    requestSuperView = (TableLayout)findViewById(R.id.request_super_view);
	    groupSuperView = (TableLayout)findViewById(R.id.group_super_view);

	    userImageView = (ImageView)findViewById(R.id.user_image);
	    userNameView = (TextView)findViewById(R.id.username);
	    addressView = (TextView)findViewById(R.id.address_text);
	    bioView = (TextView)findViewById(R.id.bio_text);
	    activityView = (TextView)findViewById(R.id.activity);
	    contactView	 = (TextView)findViewById(R.id.contact);
	    balanceView = (TextView)findViewById(R.id.balance);
	    messageBtn = (Button)findViewById(R.id.message_btn);
	    vStatementBtn = (Button)findViewById(R.id.statement_btn);
	    
	    vBadge1 = (ImageView)findViewById(R.id.badge_1);
	    vBadge2 = (ImageView)findViewById(R.id.badge_2);
	    vBadge3 = (ImageView)findViewById(R.id.badge_3);
	    vBadge4 = (ImageView)findViewById(R.id.badge_4);
	    vBadge5 = (ImageView)findViewById(R.id.badge_5);
	    vBadge6 = (ImageView)findViewById(R.id.badge_6);
	    
	    vContactEditBtn = (Button)findViewById(R.id.contact_edit_btn);
	    vAddressEditBtn = (Button)findViewById(R.id.address_edit_btn);
	    vBioEditBtn = (Button)findViewById(R.id.bio_edit_btn);
	    vGroupEditBtn = (Button)findViewById(R.id.group_edit_btn);
	    vReportHourBtn = (Button)findViewById(R.id.report_btn);
	    
	    vContactEditBtn.setVisibility(View.GONE);
	    vAddressEditBtn.setVisibility(View.GONE);
	    vBioEditBtn.setVisibility(View.GONE);
	    vGroupEditBtn.setVisibility(View.GONE);
	    
	    vShareGroup = (RadioGroup)findViewById(R.id.radioShare);
	    
	    mLoadingBar = (ProgressBar)findViewById(R.id.loading_bar);
	    mLoadingText = (TextView)findViewById(R.id.loading_text);
	    mScrollView = (ScrollView)findViewById(R.id.scroll_view);
	    mFooter = (LinearLayout)findViewById(R.id.footer);
	    vBalanceView = (LinearLayout)findViewById(R.id.balance_layout);
	    
	    if(mPref.getBoolean("sharing_location", true)) {
	    	vShareGroup.check(R.id.radioShareYes);
	    }
	    else {
	    	vShareGroup.check(R.id.radioShareNo);
	    }
	    
	    setTitle("User profile");
	    getActionBar().setIcon(R.drawable.profile);
	    //getActionBar().setDisplayHomeAsUpEnabled(true);
	    
	    if(mUserID == mPref.getInt("memID", 10017)) {
	    	
	    	vBalanceView.setVisibility(View.VISIBLE);
	    	vStatementBtn.setVisibility(View.VISIBLE);
	    	vReportHourBtn.setVisibility(View.GONE);
	    	
	    	/*
	    	vReportHourBtn.setText("Logout");
	    	vReportHourBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
				}
			});
			*/
	    	
			vStatementBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(MTBProfilePage.this, MTBStatementPage.class));
				}
			});
	    	
	    	messageBtn.setText("Show profile updates");
	    	messageBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if(mProfileEdit) {
						mProfileEdit = false;
						
						messageBtn.setText("Show profile updates");
						
						vContactEditBtn.setVisibility(View.GONE);
						vAddressEditBtn.setVisibility(View.GONE);
						vBioEditBtn.setVisibility(View.GONE);
						vGroupEditBtn.setVisibility(View.GONE);
					}
					else {
						mProfileEdit = true;
						
						messageBtn.setText("Hide profile updates");
						
						vContactEditBtn.setVisibility(View.VISIBLE);
						vAddressEditBtn.setVisibility(View.VISIBLE);
						vBioEditBtn.setVisibility(View.VISIBLE);
						vGroupEditBtn.setVisibility(View.VISIBLE);
					}
					
					// when contact edit btn clicked
					vContactEditBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							
							Intent intent = new Intent(MTBProfilePage.this, MTBProfileEditContactPage.class);
							
							startActivityForResult(intent, 0);
						}
					});
					
					// when address edit btn clicked
					vAddressEditBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							
							Intent intent = new Intent(MTBProfilePage.this, MTBProfileEditAddressPage.class);
							
							startActivityForResult(intent, 0);
						}
					});
					
					// when bio edit btn clicked
					vBioEditBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							
							Intent intent = new Intent(MTBProfilePage.this, MTBProfileEditBioPage.class);
							
							startActivityForResult(intent, 0);							
							
						}
					});
					
					// when group edit btn clicked
					vGroupEditBtn.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							
						}
					});
				}
			});
	    }
	    else {
	    	vBalanceView.setVisibility(View.GONE);
	    	vStatementBtn.setVisibility(View.GONE);
	    	vReportHourBtn.setVisibility(View.GONE);
	    	
	    	vReportHourBtn.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// show a dialog for confirmation
					new AlertDialog.Builder(MTBProfilePage.this)
					.setTitle("Message")
					.setIcon(R.drawable.hourworld_icon_30_30)
					.setMessage("Did you provide or receive the service that you want to report?")
					.setPositiveButton("Provided", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// report hours
							Intent intent = new Intent(MTBProfilePage.this, MTBReportHoursPage.class);
							intent.putExtra("Descr", "");
							intent.putExtra("userID", mUserID);
							
							// SvcCatID and SvcID are hard-coded
							intent.putExtra("SvcCatID", 1000);
							intent.putExtra("SvcID", 1009);
							
							intent.putExtra("Provided", true);
							
							startActivity(intent);
						}
					})
					.setNegativeButton("Received", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// report hours
							Intent intent = new Intent(MTBProfilePage.this, MTBReportHoursPage.class);
							intent.putExtra("Descr", "");
							intent.putExtra("userID", mUserID);
							
							// SvcCatID and SvcID are hard-coded
							intent.putExtra("SvcCatID", 1000);
							intent.putExtra("SvcID", 1009);
							
							intent.putExtra("Provided", false);
							
							startActivity(intent);
						}
					})
					.show();
				}
			});
	    }
	    
	    mScrollView.setVisibility(View.GONE);
		mLoadingBar.setVisibility(View.VISIBLE);
	    mLoadingText.setVisibility(View.VISIBLE);
	    
	    mLoadingText.setText("Loading data...");
	    
	    // initialize
	    mContact = "";
		mUsername = "";
		mProfileImage = "";
		mCity = "";
		mBio = "";
		
		mGroupArr.clear();
		mOfferArr.clear();
		mRequestArr.clear();
		
		groupSuperView.removeAllViews();
		offerSuperView.removeAllViews();
		requestSuperView.removeAllViews();

		new getProfileDetails().execute();
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_actions_profile, menu);
        
        if(mUserID == mPref.getInt("memID", 0)) {
        	menu.findItem(R.id.logout).setVisible(true);
        	menu.findItem(R.id.report).setVisible(false);
	    }
	    else {
	    	menu.findItem(R.id.logout).setVisible(false);
	    	menu.findItem(R.id.report).setVisible(true);
	    }
 
        return super.onCreateOptionsMenu(menu);
    }
	
	@Override
	public void onResume() {
		super.onResume();
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
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		int vShareBtnSelectedID = vShareGroup.getCheckedRadioButtonId();
		vShareBtn = (RadioButton)findViewById(vShareBtnSelectedID);
		mShare = vShareBtn.getText().toString();
		
		Log.i("K", "mShare: " + mShare);
		
		SharedPreferences.Editor editor = mPref.edit();
		if(mShare.equals("Yes")) {
			editor.putBoolean("sharing_location", true);
			editor.commit();
		}
		else {
			editor.putBoolean("sharing_location", false);
			editor.commit();
		}
	}
	
	class groupItem {
		public int groupID;
		public String groupName;
		public String groupProfile;
		public String groupOwner;
	}
	
	class offerRequestItem {
		public int svcCatID;
		public int svcID;
		public String svcCat;
		public String service;
		public String svcImage;
		public String svcDescription;
		public String refIcon;
		public int refCount;
		public String listRefs;
	}
	
	class getProfileDetails extends AsyncTask<Void, Integer, Boolean>{

		@Override
		protected Boolean doInBackground(Void... arg0) {
			HttpClient httpClient = new DefaultHttpClient();
			String url = new String(Constants.AUTHENTICATE);
		    HttpPost httpPost = new HttpPost(url);
		    
		    // add values and using library
		    MultipartEntity entity = new MultipartEntity();

		    try {
		    	entity.addPart("requestType", new StringBody("ProfileB," + mPref.getInt("EID", 1001) + ":" + mUserID)); // specify a type of this request
		    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
		    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
		    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
		    	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			}
		   
			httpPost.setEntity(entity);

			// output string
			String result = "";
			
			try {
				HttpResponse response = httpClient.execute(httpPost);
			
				Log.i("K", "getStatusCode (from MTBProfilePage): " + response.getStatusLine().getStatusCode());
				
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
						// get the return value
						JSONArray jAry = jObj.getJSONArray("results");
						
						// return false if there's no value
						if(jAry.length() == 0) {
							return false;
						}
						
						JSONObject jObj2 = jAry.getJSONObject(0);
						mUserID = jObj2.getInt("listMbrID");
						mUsername = jObj2.getString("listMbrName");
						mProfileImage = Constants.HOURWORLD + jObj2.getString("Profile");
						mCity = jObj2.getString("City");
						mBio = jObj2.getString("Bio");
						
						mTotalTrans = jObj2.getInt("totalTrans");
						mTotalMembers = jObj2.getInt("totalMbrs");
						mTotalReceived = jObj2.getInt("RcvCount");
						mSatisfaction = jObj2.getInt("SatPct");
						
						// handle contactArray
						if(!jObj2.getString("ContactArray").equals("null")) {
							JSONArray jContactArr = jObj2.getJSONArray("ContactArray");
							for(int i = 0 ; i < jContactArr.length() ; i++) {
								JSONObject jContactObj = jContactArr.getJSONObject(i);
								
								if(i == jContactArr.length() - 1) {
									mContact += jContactObj.getString("contactType") + ": \t" + jContactObj.getString("contactInfo");
								}
								else {
									mContact += jContactObj.getString("contactType") + ": \t" + jContactObj.getString("contactInfo") + "\n";
								}
							}
						}

						// handle groupsArray
						if(!jObj2.getString("GroupsArray").equals("null")) {
							JSONArray jGroupArr = jObj2.getJSONArray("GroupsArray");
							for(int i = 0 ; i < jGroupArr.length() ; i++) {
								JSONObject jGroupObj = jGroupArr.getJSONObject(i);
								groupItem gItem = new groupItem();
								gItem.groupID = jGroupObj.getInt("groupID");
								gItem.groupName = jGroupObj.getString("groupName");
								gItem.groupProfile = jGroupObj.getString("groupProfile");
								gItem.groupOwner = jGroupObj.getString("groupOwner");
									
								mGroupArr.add(gItem);
							}
						}
					
						// handle offersArray
						if(!jObj2.getString("OffersArray").equals("null")) {
								JSONArray jOfferArr = jObj2.getJSONArray("OffersArray");
								for(int i = 0 ; i < jOfferArr.length() ; i++) {
									JSONObject jItemObj = jOfferArr.getJSONObject(i);
		
									if(!jItemObj.getString("SvcDescr").trim().contains("Please add")) {
										
										MTBTaskItems item = new MTBTaskItems();
										item.setListMemID(mUserID);
										
										if(!jItemObj.getString("SvcDescr").equals("null") && jItemObj.getString("SvcDescr").length() > 0) {
											item.setDescription(jItemObj.getString("SvcDescr"));
										}
										else {
											item.setDescription("No description found");
										}
										
										item.setListMemName(mUsername);
										item.setProfileImage(mProfileImage);
										
										item.setSvcCatID(jItemObj.getInt("SvcCatID"));
										item.setSvcCat(jItemObj.getString("SvcCat"));
										item.setSvcID(jItemObj.getInt("SvcID"));
										item.setService(jItemObj.getString("Service"));
										
										// empty at the moment
										item.setTimeStamp("");
										item.setEmailAddress("");
										item.setPhoneNumber("");
										
										item.setOLat(0.0);
										item.setOLon(0.0);
										item.setDLat(0.0);
										item.setDLon(0.0);
										
										mOfferArr.add(item);
									}
							}
						}
						
						// handle requestsArray
						if(!jObj2.getString("RequestsArray").equals("null")) {
								JSONArray jRequestArr = jObj2.getJSONArray("RequestsArray");
								for(int i = 0 ; i < jRequestArr.length() ; i++) {
									JSONObject jItemObj = jRequestArr.getJSONObject(i);
									
									if(!jItemObj.getString("SvcDescr").trim().contains("Please add")) {
										
										MTBTaskItems item = new MTBTaskItems();
										item.setListMemID(mUserID);
										
										if(!jItemObj.getString("SvcDescr").equals("null") && jItemObj.getString("SvcDescr").length() > 0) {
											item.setDescription(jItemObj.getString("SvcDescr"));
										}
										else {
											item.setDescription("No description found");
										}
										
										item.setListMemName(mUsername);
										item.setProfileImage(mProfileImage);
										
										item.setSvcCatID(jItemObj.getInt("SvcCatID"));
										item.setSvcCat(jItemObj.getString("SvcCat"));
										item.setSvcID(jItemObj.getInt("SvcID"));
										item.setService(jItemObj.getString("Service"));
										
										// empty at the moment
										item.setTimeStamp("");
										item.setEmailAddress("");
										item.setPhoneNumber("");
										
										item.setOLat(0.0);
										item.setOLon(0.0);
										item.setDLat(0.0);
										item.setDLon(0.0);
										
										mRequestArr.add(item);
									}
								}
						}
						
						
						Log.i("K", "" + mOfferArr.size() + " " + mRequestArr.size());
						
						if(!jObj2.getString("Balances").equals("null")) {
							JSONArray jBalAry = jObj2.getJSONArray("Balances");
							JSONObject jBalObj = jBalAry.getJSONObject(0); 
							
							mProvidedHours = jBalObj.getString("provided");
							mReceivedHours = jBalObj.getString("received");
							mBalance = jBalObj.getDouble("balance");
						}
						
						// badge array
						if(!jObj2.getString("BadgeArray").equals("null")) {
							JSONArray jBadgeAry = jObj2.getJSONArray("BadgeArray");


							String[] badgeItems = jBadgeAry.getString(0).split(",");
							if(!badgeItems.equals(null) && badgeItems.length >= 10) {
								mTotalServicesLink = badgeItems[0];
								mTotalServicesTitle = badgeItems[1];
							}
							else {
								mTotalServicesLink = badgeItems[0];
								mTotalServicesTitle = badgeItems[1];
							}
							
							badgeItems = jBadgeAry.getString(1).split(",");
							if(!badgeItems.equals(null) && badgeItems.length >= 10) {
								mTotalReceivesLink = badgeItems[0];
								mTotalReceivesTitle = badgeItems[1];
							}
							else {
								mTotalReceivesLink = badgeItems[0];
								mTotalReceivesTitle = badgeItems[1];
							}
							
							badgeItems = jBadgeAry.getString(2).split(",");
							if(!badgeItems.equals(null) && badgeItems.length >= 10) {
								mTotalExcHoursLink = badgeItems[0];
								mTotalExcHoursTitle = badgeItems[1];
							}
							else {
								mTotalExcHoursLink = badgeItems[0];
								mTotalExcHoursTitle = badgeItems[1];
							}
							
							badgeItems = jBadgeAry.getString(3).split(",");
							if(!badgeItems.equals(null) && badgeItems.length >= 10) {
								mTotalRefsLink = badgeItems[0];
								mTotalRefsTitle = badgeItems[1];
							}
							else {
								mTotalRefsLink = badgeItems[0];
								mTotalRefsTitle = badgeItems[1];
							}
							
							badgeItems = jBadgeAry.getString(5).split(",");
							if(!badgeItems.equals(null) && badgeItems.length >= 10) {
								mDiversityLink = badgeItems[0];
								mDiversityTitle = badgeItems[1];
							}
							else {
								mDiversityLink = "https://hourworld.org/db_icons/Div/00.png";
								mDiversityTitle = "No exchanges with different members yet";
							}
							
							badgeItems = jBadgeAry.getString(7).split(",");
							if(!badgeItems.equals(null) && badgeItems.length >= 10) {
								mConsecMoLink = badgeItems[0];
								mConsecMoTitle = badgeItems[1];
							}
							else {
								mConsecMoLink = badgeItems[0];
								mConsecMoTitle = badgeItems[1];
							}
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
				
				// make them invisible
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setVisibility(View.GONE);
				
				mScrollView.setVisibility(View.VISIBLE);
				
				if(mUserID == mPref.getInt("memID", 10017)) {
					mFooter.setVisibility(View.VISIBLE);
				}
				else {
					mFooter.setVisibility(View.GONE);
				}
				
				// username
				if(mUserID == mPref.getInt("memID", 10017)) {
					userNameView.setText(mUsername + " (You)");
				}
				else {
					userNameView.setText(mUsername);
				}

				// user profile image
				userImageView.setTag(mProfileImage);
				imageLoader.DisplayImage(mProfileImage, MTBProfilePage.this, userImageView);
				
				// activity
				activityView.setText(mTotalTrans + " exchanges with " + mTotalMembers + " different members.\nProvided " + mTotalReceived + " times.\n" + mSatisfaction + " % Satisfied.");
				
				// contact
				contactView.setText(mContact);
				
				// address
				addressView.setText(mCity);
				
				// bio (linkify)
				bioView.setText(Html.fromHtml(mBio));
				bioView.setAutoLinkMask(Linkify.WEB_URLS);
				
				// balance
				balanceView.setText("provided:\t" + mProvidedHours + " hours\nreceived:\t" + mReceivedHours + " hours\nBalance:\t" + mBalance + " hours");
				
				// group
				for(int i = 0 ; i < mGroupArr.size() ; i++) {
					View groupView = vi.inflate(R.layout.mtb_profile_dynamic_item, null);
					groupView.setTag(mGroupArr.get(i));
					TextView description = (TextView)groupView.findViewById(R.id.description);
					TextView reference = (TextView)groupView.findViewById(R.id.ref_text);
					Button referenceBtn = (Button)groupView.findViewById(R.id.ref_btn);
					reference.setVisibility(View.GONE);
					referenceBtn.setVisibility(View.GONE);
					
					description.setText(mGroupArr.get(i).groupName);
					
					groupSuperView.addView(groupView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
				}
				
				// offer
				for(int i = 0 ; i < mOfferArr.size() ; i++) {
					// filter junk items
					if(mOfferArr.get(i).getDescription().length() < 3) {
						continue;
					}
					
					View offerView = vi.inflate(R.layout.mtb_profile_dynamic_item, null);
					offerView.setTag(mOfferArr.get(i));
					TextView description = (TextView)offerView.findViewById(R.id.description);
					TextView reference = (TextView)offerView.findViewById(R.id.ref_text);
					Button referenceBtn = (Button)offerView.findViewById(R.id.ref_btn);
					
					/*
					Log.i("K", "mOfferArr.get(i).refCount : " + mOfferArr.get(i).refCount);
					
					if(mOfferArr.get(i).refCount > 0) {
						reference.setVisibility(View.VISIBLE);
						referenceBtn.setVisibility(View.VISIBLE);
						
						if(mOfferArr.get(i).refCount == 1) {
							reference.setText(mOfferArr.get(i).refCount + " reference");
						}
						else {
							reference.setText(mOfferArr.get(i).refCount + " references");
						}
						
					}
					else {
						reference.setVisibility(View.GONE);
						referenceBtn.setVisibility(View.GONE);
					}*/
					
					reference.setVisibility(View.GONE);
					referenceBtn.setVisibility(View.GONE);
					
					if(mOfferArr.size() > 0) {
						description.setText(Html.fromHtml(mOfferArr.get(i).getDescription()));
						description.setTextColor(getResources().getColor(R.color.skype_blue));
						description.setAutoLinkMask(Linkify.WEB_URLS);
						
						offerSuperView.addView(offerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
						offerView.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								MTBTaskItems item = (MTBTaskItems) v.getTag();
								//Toast.makeText(MTBProfilePage.this, item.svcDescription, Toast.LENGTH_SHORT).show();
								
								Intent intent = new Intent(MTBProfilePage.this, MTBTaskDetailPage.class);
								intent.putExtra("EID", item.getEID());
								intent.putExtra("userID", item.getListMemID());
								intent.putExtra("username", item.getListMemName());
								intent.putExtra("SvcCat", item.getSvcCat());
								intent.putExtra("Descr", item.getDescription());
								intent.putExtra("timestamp", item.getTimeStamp());
								intent.putExtra("profileImage", item.getProfileImage());
								intent.putExtra("emailAddress", item.getEmailAddress());
								intent.putExtra("phoneNumber", item.getPhoneNumber());
								
								intent.putExtra("SvcCatID", item.getSvcCatID());
								intent.putExtra("SvcID", item.getSvcID());
								intent.putExtra("Service", item.getService());
								
								intent.putExtra("latitude", item.getLatitude());
								intent.putExtra("longitude", item.getLongitude());
								
								intent.putExtra("oLat", item.getOLat());
								intent.putExtra("oLon", item.getOLon());
								intent.putExtra("dLat", item.getDLat());
								intent.putExtra("dLon", item.getDLon());
								
								intent.putExtra("isOffer", "T");
								intent.putExtra("isRequest", "F");
								
								startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
							}
						});
					}
				}
				
				// request
				for(int i = 0 ; i < mRequestArr.size() ; i++) {
					// filter junk items
					if(mRequestArr.get(i).getDescription().length() < 3) {
						continue;
					}
					
					View requestView = vi.inflate(R.layout.mtb_profile_dynamic_item, null);
					requestView.setTag(mRequestArr.get(i));
					TextView description = (TextView)requestView.findViewById(R.id.description);
					TextView reference = (TextView)requestView.findViewById(R.id.ref_text);
					Button referenceBtn = (Button)requestView.findViewById(R.id.ref_btn);
					
					/*
					if(mRequestArr.get(i).refCount > 0) {
						reference.setVisibility(View.VISIBLE);
						referenceBtn.setVisibility(View.VISIBLE);
						
						if(mRequestArr.get(i).refCount == 1) {
							reference.setText(mRequestArr.get(i).refCount + " reference");
						}
						else {
							reference.setText(mRequestArr.get(i).refCount + " references");
						}
					}
					else {
						reference.setVisibility(View.GONE);
						referenceBtn.setVisibility(View.GONE);
					}
					*/
					
					reference.setVisibility(View.GONE);
					referenceBtn.setVisibility(View.GONE);
					
					if(mRequestArr.size() > 0) {
						description.setText(Html.fromHtml(mRequestArr.get(i).getDescription()));
						description.setTextColor(getResources().getColor(R.color.skype_blue));
						description.setAutoLinkMask(Linkify.WEB_URLS);
						
						requestSuperView.addView(requestView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
						requestView.setOnClickListener(new View.OnClickListener() {
							
							@Override
							public void onClick(View v) {
								MTBTaskItems item = (MTBTaskItems) v.getTag();
								//Toast.makeText(MTBProfilePage.this, item.svcDescription, Toast.LENGTH_SHORT).show();
								
								Intent intent = new Intent(MTBProfilePage.this, MTBTaskDetailPage.class);
								intent.putExtra("EID", item.getEID());
								intent.putExtra("userID", item.getListMemID());
								intent.putExtra("username", item.getListMemName());
								intent.putExtra("SvcCat", item.getSvcCat());
								intent.putExtra("Descr", item.getDescription());
								intent.putExtra("timestamp", item.getTimeStamp());
								intent.putExtra("profileImage", item.getProfileImage());
								intent.putExtra("emailAddress", item.getEmailAddress());
								intent.putExtra("phoneNumber", item.getPhoneNumber());
								
								intent.putExtra("SvcCatID", item.getSvcCatID());
								intent.putExtra("SvcID", item.getSvcID());
								intent.putExtra("Service", item.getService());
								
								intent.putExtra("latitude", item.getLatitude());
								intent.putExtra("longitude", item.getLongitude());
								
								intent.putExtra("oLat", item.getOLat());
								intent.putExtra("oLon", item.getOLon());
								intent.putExtra("dLat", item.getDLat());
								intent.putExtra("dLon", item.getDLon());
								
								intent.putExtra("isOffer", "F");
								intent.putExtra("isRequest", "T");
								
								startActivityForResult(intent, Constants.MTB_DETAIL_PAGE);
							}
						});
					}
				}
				
				// show badges
				vBadge1.setTag(mTotalServicesLink);
				imageLoader.DisplayImage(mTotalServicesLink, MTBProfilePage.this, vBadge1);
				
				vBadge2.setTag(mTotalReceivesLink);
				imageLoader.DisplayImage(mTotalReceivesLink, MTBProfilePage.this, vBadge2);
				
				vBadge3.setTag(mTotalExcHoursLink);
				imageLoader.DisplayImage(mTotalExcHoursLink, MTBProfilePage.this, vBadge3);
				
				vBadge4.setTag(mTotalRefsLink);
				imageLoader.DisplayImage(mTotalRefsLink, MTBProfilePage.this, vBadge4);
				
				vBadge5.setTag(mDiversityLink);
				imageLoader.DisplayImage(mDiversityLink, MTBProfilePage.this, vBadge5);
				
				vBadge6.setTag(mConsecMoLink);
				imageLoader.DisplayImage(mConsecMoLink, MTBProfilePage.this, vBadge6);
				
				vBadge1.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						
						new AlertDialog.Builder(MTBProfilePage.this)
				   		.setTitle("Badge")
				   		.setMessage(mTotalServicesTitle)
				   		.setPositiveButton("Close", null)
				   		.show();
					}
				});
				
				vBadge2.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(MTBProfilePage.this)
				   		.setTitle("Badge")
				   		.setMessage(mTotalReceivesTitle)
				   		.setPositiveButton("Close", null)
				   		.show();
					}
				});
				
				vBadge3.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(MTBProfilePage.this)
				   		.setTitle("Badge")
				   		.setMessage(mTotalExcHoursTitle)
				   		.setPositiveButton("Close", null)
				   		.show();
					}
				});
				
				vBadge4.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(MTBProfilePage.this)
				   		.setTitle("Badge")
				   		.setMessage(mTotalRefsTitle)
				   		.setPositiveButton("Close", null)
				   		.show();
					}
				});
				
				vBadge5.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(MTBProfilePage.this)
				   		.setTitle("Badge")
				   		.setMessage(mDiversityTitle)
				   		.setPositiveButton("Close", null)
				   		.show();
					}
				});
				
				vBadge6.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						new AlertDialog.Builder(MTBProfilePage.this)
				   		.setTitle("Badge")
				   		.setMessage(mConsecMoTitle)
				   		.setPositiveButton("Close", null)
				   		.show();
					}
				});
				
			}
			else {
				mLoadingBar.setVisibility(View.GONE);
				mLoadingText.setText("Error while loading. Please try again.");
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		if(resultCode == RESULT_OK && requestCode == 0) {
			if(data.getExtras().getBoolean("refresh")) {

			}
			else {
				
			}
		}
		else if(resultCode == RESULT_OK && requestCode == 1){
			
			Log.i("K", "Logging out from the app");
			
			Intent intent = new Intent(MTBProfilePage.this, MTBLoginPage.class);
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
			dialog = ProgressDialog.show(MTBProfilePage.this, "Logging out...", "Please wait...", true);
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
			
			MTBUploadHandler upload = new MTBUploadHandler(MTBProfilePage.this);
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
								
				Intent intent = new Intent(MTBProfilePage.this, MTBLoginPage.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); 
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        	intent.putExtra("prev_activity", Constants.FROM_BEGINNING);
	        	startActivity(intent);
		    	finish();
				
				//finish();
				
			}
			else {
				Toast.makeText(MTBProfilePage.this, "Failed to logout. Please try again", Toast.LENGTH_SHORT).show();
			}
		}
	}
	
	class DeleteCache extends AsyncTask<Void, Void, Void> {

		ProgressDialog dialog;
		
		@Override
		public void onPreExecute() {
			dialog = ProgressDialog.show(MTBProfilePage.this, "Deleting...", "Please wait...", true);
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
	
	/**
     * On selecting action bar icons
     * */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        if(item.getItemId() == R.id.logout) {
            // logout action
        	logout();
            return true;
        }
        else {
            return super.onOptionsItemSelected(item);
        }
    }
    
    public void logout() {
    	new AlertDialog.Builder(MTBProfilePage.this)
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
}
