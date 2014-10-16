package edu.psu.ist.mtb_hourworld.utilities;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.util.Log;

public class MTBTimeConverter {

	public String normalFormat(long requestTime) {
		/*
		 *  SimpleDateFormat sdf = new SimpleDateFormat("h:mm a, MMM dd");
        	Date resultdate = new Date(Long.parseLong(mArr.get(position).getPubDate()));
        	holder.rssPubDate.setText(sdf.format(resultdate));//  + "," + mArr.get(position).getPubDate());
		 */
		
		DateFormat dFormatter = new SimpleDateFormat("h:mm a, MMM dd");
		dFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        Date resultdate = new Date(requestTime * 1000);
	
        return dFormatter.format(resultdate);
		
	}
	
	public int dateCompare(long date1, long date2) {
		DateFormat dFormatter = new SimpleDateFormat("h:mm a, MMM dd");
		dFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        Date resultDate1 = new Date(date1 * 1000);
        Date resultDate2 = new Date(date2 * 1000);
        
        Log.i("K", "dFormatter.format(resultDate1) : " + dFormatter.format(resultDate1));
        Log.i("K", "dFormatter.format(resultDate2) : " + dFormatter.format(resultDate2));
        
        return dFormatter.format(resultDate1).compareTo(dFormatter.format(resultDate2));
	}
	
	public String onlyTime(long requestTime) {
		
		DateFormat dFormatter = new SimpleDateFormat("hh:mm a");
		dFormatter.setTimeZone(TimeZone.getTimeZone("EST"));
        Date resultdate = new Date(requestTime * 1000);
	
        return dFormatter.format(resultdate);
		
	}
	
	public String CertainDaysAgo(long requestTime) {
		String returnValue = "";

		long currentTime = System.currentTimeMillis() / 1000;
		long oneDayTime = 3600 * 24;
		
		int dayConvert = (int)((currentTime - requestTime) / oneDayTime);
		
		if(dayConvert == 0) {
			int minConvert = (int)((currentTime - requestTime) % oneDayTime);
			
			int hourConvert = (int)(minConvert / 3600);
			if(hourConvert > 0) {
				if(hourConvert == 1) {
					returnValue = hourConvert + " hour ago";
				}
				else {
					returnValue = hourConvert + " hours ago";
				}
			}
			else {
				minConvert = (int)((minConvert - hourConvert * 3600) / 60);
				
				if(minConvert == 1) {
					returnValue = "1 minute ago";
				}
				else {
					returnValue = minConvert + " minutes ago";
				}
			}
			
			
		}
		else {
			if(dayConvert == 1) {
				returnValue = dayConvert + " day ago";
			}
			else {
				returnValue = dayConvert + " days ago";
			}
			
		}
		
		return returnValue;
	}
	
	
	
}
