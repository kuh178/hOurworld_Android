package edu.psu.ist.mtb_hourworld.location;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;


public class MobileTimeBankingOverlayItem extends OverlayItem {
	private Drawable marker = null;
	private boolean isHeart = false;
	private Drawable heart = null;
	private int mExchangeID = 0;

	public MobileTimeBankingOverlayItem(GeoPoint pt, int id, String name, String snippet, Drawable marker) {
		super(pt, name, snippet);
		
		Log.i("K", "snippet : " + snippet);
		
		mExchangeID = id;
		this.marker = marker;
		this.heart = marker;
	}

	@Override
	public Drawable getMarker(int stateBitset) {
		Drawable result = (isHeart ? heart : marker);
		
		setState(result, stateBitset);
	
		return(result);
	}
	
	public void toggleHeart() {
		isHeart=!isHeart;
	}
	
	public int getID() {
		return mExchangeID;
	}
}	

