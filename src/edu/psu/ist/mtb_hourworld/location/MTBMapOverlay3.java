package edu.psu.ist.mtb_hourworld.location;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;

import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.tasks.MTBTaskDetailPage;

public class MTBMapOverlay3 extends BalloonItemizedOverlay<MobileTimeBankingOverlayItem>{
	private Context mContext;
	private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
	private ArrayList<MobileTimeBankingOverlayItem> mOverlayArr = new ArrayList<MobileTimeBankingOverlayItem>();
	
	private Drawable mMarker;
	private MapView mMap = null;
	
	private int mFlag = 0;

	/*
	 * from special event page
	 */
	public MTBMapOverlay3(MapView mapView, Drawable defaultMarker, ArrayList<MTBTaskItems> arr, int flag) {
		super(boundCenterBottom(defaultMarker), mapView, 0);
		
		mContext = mapView.getContext();
		mMarker = defaultMarker;
		mArr.addAll(arr);
		
		mFlag = flag;
	
		for(int i = 0 ; i < mArr.size() ; i++) {
			GeoPoint geoPoint = getPoint(mArr.get(i).getDLat(), mArr.get(i).getDLon());
			
			if(mArr.size() == 0) {
				// do nothing
			}
			else {
				
				String description = mArr.get(i).getDescription();
				if(description.length() < 20)
					description = description.substring(0, description.length()-1);
				else {
					description = description.substring(0, 19) + "...";
				}
				
				mOverlayArr.add(new MobileTimeBankingOverlayItem(geoPoint, 
						mArr.get(i).getSvcID(), 
						description,
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

		MTBTaskItems item = mArr.get(index);
		
		Intent intent = new Intent(mContext, MTBTaskDetailPage.class);
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
		
		if(mFlag == 0) {
			intent.putExtra("isOffer", "T");
		}
		else {
			intent.putExtra("isOffer", "F");
		}
		
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
