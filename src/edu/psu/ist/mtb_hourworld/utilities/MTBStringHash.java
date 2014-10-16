package edu.psu.ist.mtb_hourworld.utilities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import android.util.Log;

/**
 * 
 * @author kyungsikhan
 * To create and md5 hash
 *
 */

public class MTBStringHash {
	
	public String createChallenge() {
		String challenge = "";
		Random r = new Random();
		
		for(int i = 0 ; i < 80 ; i++) {
			challenge += r.nextInt(15-0) + 0;
		}
		
		return challenge;
	}
	
	public String md5(final String inputValue) {
		
		Log.i("K" ,"string input for md5 : " + inputValue);
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
			
			md.update(inputValue.getBytes());
			 
	        byte byteData[] = md.digest();
	 
	        //convert the byte to hex format method 1
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	         sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }
	 
	        //convert the byte to hex format method 2
	        StringBuffer hexString = new StringBuffer();
	    	for (int i = 0 ; i < byteData.length ; i++) {
	    		String hex=Integer.toHexString(0xff & byteData[i]);
	   	     	if(hex.length()==1) hexString.append('0');
	   	     	hexString.append(hex);
	    	}
	    	return hexString.toString();
			
			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return "";
        
	}
}
