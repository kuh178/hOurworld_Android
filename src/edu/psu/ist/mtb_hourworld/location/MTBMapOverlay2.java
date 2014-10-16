package edu.psu.ist.mtb_hourworld.location;

import java.util.ArrayList;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import edu.psu.ist.mtb_hourworld.items.MTBTaskItems;
import edu.psu.ist.mtb_hourworld.tasks.MTBMessageDetailPage;

public class MTBMapOverlay2 extends BalloonItemizedOverlay<MobileTimeBankingOverlayItem>{
	private Context mContext;
	private ArrayList<MTBTaskItems> mArr = new ArrayList<MTBTaskItems>();
	private ArrayList<MobileTimeBankingOverlayItem> mOverlayArr = new ArrayList<MobileTimeBankingOverlayItem>();
	
	private Drawable mMarker;
	private MapView mMap = null;

	/*
	 * from special event page
	 */
	public MTBMapOverlay2(MapView mapView, Drawable defaultMarker, ArrayList<MTBTaskItems> arr) {
		super(boundCenterBottom(defaultMarker), mapView, 0);
		
		mContext = mapView.getContext();
		mMarker = defaultMarker;
		mArr.addAll(arr);
		
		for(int i = 0 ; i < mArr.size() ; i++) {
			GeoPoint geoPoint = getPoint(mArr.get(i).getDLat(), mArr.get(i).getDLon());
			
			if(mArr.size() == 0) {
				// do nothing
			}
			else {
				
				String eBlast = mArr.get(i).getEblast();
				if(eBlast.length() < 30)
					eBlast = eBlast.substring(0, eBlast.length()-1);
				else {
					eBlast = eBlast.substring(0, 29) + "...";
				}
				
				mOverlayArr.add(new MobileTimeBankingOverlayItem(geoPoint, 
						mArr.get(i).getPostNum(), 
						eBlast,
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

		Intent intent = new Intent(mContext, MTBMessageDetailPage.class);
		intent.putExtra("EID", mArr.get(index).getEID());
		intent.putExtra("postID", mArr.get(index).getPostNum());
		intent.putExtra("userID", mArr.get(index).getListMemID());
		intent.putExtra("username", mArr.get(index).getListMemName());
		intent.putExtra("SvcCat", mArr.get(index).getSvcCat());
		intent.putExtra("Descr", mArr.get(index).getEblast());
		intent.putExtra("timestamp", mArr.get(index).getTimeStamp());
		intent.putExtra("profileImage", mArr.get(index).getProfileImage());
		intent.putExtra("emailAddress", mArr.get(index).getEmailAddress());
		intent.putExtra("phoneNumber", mArr.get(index).getPhoneNumber());
		intent.putExtra("oLat", mArr.get(index).getOLat());
		intent.putExtra("oLon", mArr.get(index).getOLon());
		intent.putExtra("dLat", mArr.get(index).getDLat());
		intent.putExtra("dLon", mArr.get(index).getDLon());
		
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
