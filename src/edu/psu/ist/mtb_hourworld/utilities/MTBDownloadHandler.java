package edu.psu.ist.mtb_hourworld.utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import android.util.Log;

public class MTBDownloadHandler {

	//Download JSON formatted data
	/*
	 * Nice tutorial: http://www.vogella.de/articles/AndroidNetworking/article.html
	 */
	public String downloadJson(String urlAddr){
		StringBuilder result = new StringBuilder();
		
		Log.i("K", "IN DOWNLOAD JSON : " + urlAddr);
		
		try{
			URL url = new URL(urlAddr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			if(conn != null){
				conn.setConnectTimeout(30 * 1000);
				conn.setReadTimeout(30 * 1000);
				conn.setUseCaches(false);
				if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
					BufferedReader br = new BufferedReader(
							new InputStreamReader(conn.getInputStream()));
					for(;;){
						String line = br.readLine();
						if (line == null) 
							break;
						result.append(line+'\n');
					}
					
					Log.i("K", "RESULT : " + result);
					
					br.close();
				}
				else{
					conn.disconnect();
					return null;
				}
				conn.disconnect();
			}
		}
		catch(MalformedURLException e){
			Log.e("K", "Download JSON: " + e.toString());
			return null;
		}
		catch(ProtocolException e){
			Log.e("K", "Download JSON: " + e.toString());
			return null;
		}
		catch(IOException e){
			Log.e("K", "Download JSON: " + e.toString());
			return null;
		}
		return result.toString();
	}
}
