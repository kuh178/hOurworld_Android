package edu.psu.ist.mtb_hourworld.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import edu.psu.ist.mtb_hourworld.constants.Constants;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.util.Log;

public class MTBUploadHandler {
	
	private SharedPreferences mPref;
	
	public MTBUploadHandler(Context context) {
		mPref = PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	public boolean deleteItem(int svcCatID, int svcID) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "Deleting the item");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("DelTask," + svcCatID + ":" + svcID)); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
        	
	    	Log.i("K", "requestType: " + "DelTask," + svcCatID + ":" + svcID);
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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	public boolean addMessage(String message, double oLat, double oLon, double dLat, double dLon, int xDay) {
		HttpClient httpClient = new DefaultHttpClient();  	
		String url = new String(Constants.AUTHENTICATE);
	    
	    HttpPost httpPost = new HttpPost(url);
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("AddMsg,0")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("message", new StringBody(message));
	    	entity.addPart("oLat", new StringBody(Double.toString(oLat)));
	    	entity.addPart("oLon", new StringBody(Double.toString(oLon)));
	    	entity.addPart("dLat", new StringBody(Double.toString(dLat)));
	    	entity.addPart("dLon", new StringBody(Double.toString(dLon)));
	    	entity.addPart("xDays", new StringBody(Integer.toString(xDay)));
	    	
	    	Log.i("K", "requestType: " + "AddMsg,0");
	    	Log.i("K", "message: " + message);
	    	Log.i("K", "oLat: " + oLat);
	    	Log.i("K", "oLon: " + oLon);
	    	Log.i("K", "dLat: " + dLat);
	    	Log.i("K", "dLon: " + dLon);
	    	
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
	
	public boolean addOfferRequest(int svcCatID, int svcID, String isOffer, String isRequest, String description, String expire, double oLat, double oLon, double dLat, double dLon) {
		HttpClient httpClient = new DefaultHttpClient();  	
		//String url = new String(Constants.REGISTER_LINK);
		String url = new String(Constants.AUTHENTICATE);
	    
	    HttpPost httpPost = new HttpPost(url);
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("AddTask,SAVE")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("SvcCatID", new StringBody(Integer.toString(svcCatID)));
	    	entity.addPart("SvcID", new StringBody(Integer.toString(svcID)));
	    	entity.addPart("Offer", new StringBody(isOffer));
	    	entity.addPart("Request", new StringBody(isRequest));
	    	entity.addPart("Descr", new StringBody(description));
	    	entity.addPart("Expire", new StringBody(expire));
	    	entity.addPart("oLat", new StringBody(Double.toString(oLat)));
	    	entity.addPart("oLon", new StringBody(Double.toString(oLon)));
	    	entity.addPart("dLat", new StringBody(Double.toString(dLat)));
	    	entity.addPart("dLon", new StringBody(Double.toString(dLon)));
	    	
	    	Log.i("K", "requestType: " + "AddTask,SAVE");
	    	Log.i("K", "SvcCatID: " + svcCatID);
	    	Log.i("K", "SvcID: " + svcID);
	    	Log.i("K", "Offer: " + isOffer);
	    	Log.i("K", "Request: " + isRequest);
	    	Log.i("K", "Descr: " + description);
	    	Log.i("K", "oLat: " + oLat);
	    	Log.i("K", "oLon: " + oLon);
	    	Log.i("K", "dLat: " + dLat);
	    	Log.i("K", "dLon: " + dLon);
	    	
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
	
	public boolean editOfferRequest(int svcCatID, int svcID, String isOffer, String isRequest, String description, double latitude, double longitude, double oLat, double oLon, double dLat, double dLon) {
		HttpClient httpClient = new DefaultHttpClient();  	
		String url = new String(Constants.AUTHENTICATE);
	    
	    HttpPost httpPost = new HttpPost(url);
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditTask,SAVE")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("SvcCatID", new StringBody(Integer.toString(svcCatID)));
	    	entity.addPart("SvcID", new StringBody(Integer.toString(svcID)));
	    	entity.addPart("Offer", new StringBody(isOffer));
	    	entity.addPart("Request", new StringBody(isRequest));
	    	entity.addPart("Descr", new StringBody(description));
	    	entity.addPart("Expire", new StringBody("365"));
	    	entity.addPart("lat", new StringBody(Double.toString(latitude)));
	    	entity.addPart("lon", new StringBody(Double.toString(longitude)));
	    	entity.addPart("oLat", new StringBody(Double.toString(oLat)));
	    	entity.addPart("oLon", new StringBody(Double.toString(oLon)));
	    	entity.addPart("dLat", new StringBody(Double.toString(dLat)));
	    	entity.addPart("dLon", new StringBody(Double.toString(dLon)));
	    	
	    	Log.i("K", "requestType: " + "EditTask,SAVE");
	    	Log.i("K", "SvcCatID: " + svcCatID);
	    	Log.i("K", "SvcID: " + svcID);
	    	Log.i("K", "Offer: " + isOffer);
	    	Log.i("K", "Request: " + isRequest);
	    	Log.i("K", "Descr: " + description);
	    	Log.i("K", "latitude: " + latitude);
	    	Log.i("K", "longitude: " + longitude);
	    	Log.i("K", "oLat: " + oLat);
	    	Log.i("K", "oLon: " + oLon);
	    	Log.i("K", "dLat: " + dLat);
	    	Log.i("K", "dLon: " + dLon);
	    	
	    	
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
	
	public boolean deleteMessage(int postID, int memID) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "Deleting the item");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("DelMsg," + postID + ":" + memID)); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
        	
	    	Log.i("K", "requestType: " + "DelMsg," + postID + ":" + memID);
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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	
	public boolean replyMessage(int postID, int memID, String message) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "Deleting the item");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("ReplyMsg," + postID + ":" + memID)); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("message", new StringBody(message));
        	
	    	Log.i("K", "requestType: " + "ReplyMsg," + postID + ":" + memID);
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "message: " + message);
	    	
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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	
	
	public boolean updateBio(String bio) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "Deleting the item");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditBio,SAVE")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("Bio", new StringBody(bio));
        	
	    	Log.i("K", "requestType: " + "EditBio,SAVE");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "Bio: " + bio);
	    	
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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	
	public boolean updateAddress(String lastname, String firstname, String street, String city, String state, String zip, String birthdate, String workplace) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "Deleting the item");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditAddress,SAVE")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("Lastname", new StringBody(lastname));
	    	entity.addPart("Firstname", new StringBody(firstname));
	    	entity.addPart("Street", new StringBody(street));
	    	entity.addPart("City", new StringBody(city));
	    	entity.addPart("State", new StringBody(state));
	    	entity.addPart("Zip", new StringBody(zip));
	    	entity.addPart("BirthDate", new StringBody(birthdate));
	    	entity.addPart("WorkPlace", new StringBody(workplace));
	    	
	    	Log.i("K", "requestType: EditAddress,SAVE");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "Lastname: " + lastname);
	    	Log.i("K", "Firstname: " + firstname);
	    	Log.i("K", "Street: " + street);
	    	Log.i("K", "City: " + city);
	    	Log.i("K", "State: " + state);
	    	Log.i("K", "Zip: " + zip);
	    	Log.i("K", "BirthDate: " + birthdate);
	    	Log.i("K", "WorkPlace: " + workplace);

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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	public boolean updateContact(String type, String contact, String privateInfo) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "update the contact info");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditContact,SAVE")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("Type", new StringBody(type));
	    	entity.addPart("Contact", new StringBody(contact));
	    	entity.addPart("Private", new StringBody(privateInfo));
	    	
	    	Log.i("K", "requestType: EditAddress,SAVE");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "Type: " + type);
	    	Log.i("K", "Contact: " + contact);
	    	Log.i("K", "Private: " + privateInfo);

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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	public boolean createGroup(String groupName, String groupDescription, int ownerID) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "update the contact info");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditGroups,NEW")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("Group", new StringBody(groupName));
	    	entity.addPart("Profile", new StringBody(groupDescription));
	    	entity.addPart("Owner", new StringBody(Integer.toString(ownerID)));
	    	
	    	Log.i("K", "requestType: EditGroups,NEW");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "Group: " + groupName);
	    	Log.i("K", "Profile: " + groupDescription);
	    	Log.i("K", "Owner: " + ownerID);

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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}

	
	public boolean sendGroupMessage(int groupID, String groupMessage) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "update the contact info");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditGroups,MSG")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("groupID", new StringBody(Integer.toString(groupID)));
	    	entity.addPart("groupMsg", new StringBody(groupMessage));
	    	
	    	
	    	Log.i("K", "requestType: EditGroups,MSG");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "groupID: " + groupID);
	    	Log.i("K", "groupMessage: " + groupMessage);

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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	public boolean joinGroup(int groupID) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "update the contact info");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditGroups,ADD")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("groupID", new StringBody(Integer.toString(groupID)));
	    	
	    	Log.i("K", "requestType: EditGroups,ADD");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "groupID: " + groupID);

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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	public boolean leaveGroup(int groupID) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "update the contact info");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("EditGroups,DEL")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	entity.addPart("groupID", new StringBody(Integer.toString(groupID)));
	    	
	    	Log.i("K", "requestType: EditGroups,DEL");
	    	Log.i("K", "accessToken: " + mPref.getString("access_token", ""));
	    	Log.i("K", "EID: " + mPref.getInt("EID", 0));
	    	Log.i("K", "memID: " + mPref.getInt("memID", 0));
	    	Log.i("K", "groupID: " + groupID);

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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
	
	public boolean logout() {
		if (Looper.myLooper() == null)
			Looper.prepare();
		
		HttpClient httpClient = new DefaultHttpClient();  		    
	    String url = new String(Constants.AUTHENTICATE);
	    HttpPost httpPost = new HttpPost(url);
	    
	    Log.i("K", "logout from the app");
	    
	    // add values and using library
	    MultipartEntity entity = new MultipartEntity();
	    
	    try {
	    	entity.addPart("requestType", new StringBody("Logout,0")); // specify a type of this request
	    	entity.addPart("accessToken", new StringBody(mPref.getString("access_token", ""))); // send the access_token
	    	entity.addPart("EID", new StringBody(Integer.toString(mPref.getInt("EID", 0))));
	    	entity.addPart("memID", new StringBody(Integer.toString(mPref.getInt("memID", 0))));
	    	
	    	Log.i("K", "requestType: Logout,0");
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
				
				JSONObject jObj;
				try {
					jObj = new JSONObject(result);
				
					if(jObj.getBoolean("success")){
						return true;
					}
					else {
						return false;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} 
		} catch (ClientProtocolException e) {
			return false;
			
		} catch (IOException e) {
			return false;
		}
		
		return false;
	}
	
}