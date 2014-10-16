package edu.psu.ist.mtb_hourworld.location;


import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import edu.psu.ist.mtb_hourworld.account.MTBRegisterPage.MTBExchange;
import edu.psu.ist.mtb_hourworld.account.MTBRegisterPage2;

public class MobileTimeBankingMapOverlay extends BalloonItemizedOverlay<MobileTimeBankingOverlayItem>{
	private Context mContext;
	private ArrayList<MTBExchange> mArr = new ArrayList<MTBExchange>();
	private ArrayList<MobileTimeBankingOverlayItem> mOverlayArr = new ArrayList<MobileTimeBankingOverlayItem>();
	
	private Drawable mMarker;
	private MapView mMap = null;

	/*
	 * from special event page
	 */
	public MobileTimeBankingMapOverlay(MapView mapView, Drawable defaultMarker, ArrayList<MTBExchange> arr) {
		super(boundCenterBottom(defaultMarker), mapView, 0);
		
		mContext = mapView.getContext();
		mMarker = defaultMarker;
		mArr.addAll(arr);
		
		for(int i = 0 ; i < mArr.size() ; i++) {
			GeoPoint geoPoint = getPoint(mArr.get(i).mLatitude, mArr.get(i).mLongitude);
			
			if(mArr.size() == 0) {
				// do nothing
			}
			else {
				Log.i("K", "mExchangeMemName : " + mArr.get(i).mExchangeMemName);
				
				mOverlayArr.add(new MobileTimeBankingOverlayItem(geoPoint, 
						mArr.get(i).mExchangeMemID, 
						mArr.get(i).mExchangeMemName,
						"",
						mMarker));
			}
		}
		
		populate();
	}

	@Override
	protected MobileTimeBankingOverlayItem createItem(int i) {
		return(mOverlayArr.get(i));
	}
	
	private GeoPoint getPoint(double lat, double lon) {
		return(new GeoPoint((int)(lat * 1E6), (int)(lon * 1E6)));
	}

	/**
	 * Handle when the user press the balloon button.
	 */
	@Override
	protected boolean onBalloonTap(final int index) {
		
		Intent intent = new Intent(mContext, MTBRegisterPage2.class);
		intent.putExtra("exchangeID", mArr.get(index).mExchangeMemID);
		mContext.startActivity(intent);

		hideBalloon();
		
		return true;
	}

	
	@Override
	public int size() {
		return(mOverlayArr.size());
	}
	
	
	void toggleHeart() {
		MobileTimeBankingOverlayItem focus = getFocus();
		
		if (focus!=null) {
			focus.toggleHeart();
		}
		
		mMap.invalidate();
	}
}
