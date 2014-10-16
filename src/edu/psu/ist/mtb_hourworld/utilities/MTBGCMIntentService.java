package edu.psu.ist.mtb_hourworld.utilities;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import com.google.android.gcm.GCMBaseIntentService;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class MTBGCMIntentService extends GCMBaseIntentService{

	public MTBGCMIntentService() {
		super(Constants.PROJECT_ID_GCM);
		Log.i("K", "GCMIntentService init");
	}

	// Called when your server sends a message to GCM, and GCM delivers it to the device.
	@Override
	protected void onMessage(Context context, Intent intent) {
		
		String message = intent.getStringExtra("message");
		String type = intent.getStringExtra("type");
		int otherUserID = Integer.parseInt(intent.getStringExtra("user_id"));
		int userImageID = Integer.parseInt(intent.getStringExtra("user_image_id"));
		
		createNotification(context, message, type, otherUserID, userImageID, intent);
	}
	
	public void createNotification(Context context, String message, String type, int otherUserID, int userImageID, Intent in) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.hourworld_icon_30_30, message, System.currentTimeMillis());
		// Hide the notification after its selected
		notification.flags |= Notification.FLAG_AUTO_CANCEL;

		/*
		if(type.equals("action_1")) {
			
			Intent intent = new Intent(context, MobileTimeBankingStatusPage.class);
			intent.putExtra("user_id", otherUserID);
			intent.putExtra("fb_auth", in.getStringExtra("fb_auth"));
			intent.putExtra("user_image_id", userImageID);
			intent.putExtra("post_id", Integer.parseInt(in.getStringExtra("post_id")));
			intent.putExtra("other_user_name", in.getStringExtra("other_user_name"));
			intent.putExtra("user_image_id", userImageID);
			intent.putExtra("f_time", Long.parseLong(in.getStringExtra("f_time")));
			intent.putExtra("t_time", Long.parseLong(in.getStringExtra("t_time")));
			intent.putExtra("flag", 1);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			notification.setLatestEventInfo(context, "Timebank", "Message from Timebank", pendingIntent);
			notificationManager.notify(0, notification);
		}
		else if(type.equals("action_2")) {
			
			Intent intent = new Intent(context, MobileTimeBankingApprovedPage.class);
			intent.putExtra("message", message);
			intent.putExtra("user_image_id", userImageID);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			notification.setLatestEventInfo(context, "Timebank", "Message from Timebank", pendingIntent);
			notificationManager.notify(0, notification);
		}
		else if(type.equals("action_3")) {
			// evaluate
			
		}
		// from a messaging function
		else if(type.equals("message")) {
			
			Intent intent = new Intent(context, MobileTimeBankingMessageMain.class);
			intent.putExtra("other_user_id", otherUserID);
			intent.putExtra("user_image_id", userImageID);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			notification.setLatestEventInfo(context, "Timebank", message, pendingIntent);
			notificationManager.notify(0, notification);
		}
		
		// from a messaging function
		else if(type.equals("group_join_request")) {
			
			String messageStr[] = message.split("//");
			
			Intent intent = new Intent(context, MobileTimeBankingGroupJoinRequestAdmin.class);
			intent.putExtra("user_id", otherUserID);
			intent.putExtra("user_image_id", userImageID);
			intent.putExtra("group_id", Integer.parseInt(messageStr[0]));
			intent.putExtra("message", messageStr[1]);
			
			PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
			notification.setLatestEventInfo(context, "Timebank", messageStr[1], pendingIntent);
			notificationManager.notify(0, notification);
		}
		*/
	}

	@Override
	protected void onRegistered(Context context, String regID) {
		// TODO Auto-generated method stub
		
		Log.i("K", "called!! + " + regID);
		// send regId to your server
		sendRegistrarationToServer(context, regID);
	}

	@Override
	protected void onUnregistered(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onError(Context arg0, String arg1) {
		// TODO Auto-generated method stub
		
	}
	
	
	private void sendRegistrarationToServer(Context context, String regID) {
		
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
		int userID = pref.getInt("user_id", 0);
		
		HttpClient httpclient = new DefaultHttpClient();  		    
	    String url = new String(Constants.GCM_REGISTRATION + "?registration_id=" + regID + "&user_id=" + userID);
	    HttpPost httppost = new HttpPost(url);
	    
	    Log.i("K", "url : " + url);

	    HttpResponse response = null;
	    try {
			response = httpclient.execute(httppost);
			
			if (response != null) {
				if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					Log.i("K", "Add Successfully");
				} 
				else {
					Log.i("K", "Error");
				}
			} 
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			Log.i("K", "ClientProtocolException during CivicinityC2DMHandler");
			
		} catch (IOException e) {
			e.printStackTrace();
			Log.i("K", "IOException during CivicinityC2DMHandler");
		}
	}

}

