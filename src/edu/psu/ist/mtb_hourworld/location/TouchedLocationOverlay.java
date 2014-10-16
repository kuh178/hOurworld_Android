package edu.psu.ist.mtb_hourworld.location;

import java.util.ArrayList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class TouchedLocationOverlay extends ItemizedOverlay<OverlayItem> {
	 
    private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
    private Handler handler;
    private boolean isOrigin;
 
    public TouchedLocationOverlay(Drawable defaultMarker, Handler h, boolean isOrigin) {
        super(boundCenterBottom(defaultMarker));
 
        // Handler object instantiated in the class MainActivity
        this.handler = h;
        this.isOrigin = isOrigin;
    }
 
    // Executed, when populate() method is called
    @Override
    protected OverlayItem createItem(int arg0) {
        return mOverlays.get(arg0);
    }
 
    @Override
    public int size() {
        return mOverlays.size();
    }
 
    public void addOverlay(OverlayItem overlay){
        mOverlays.add(overlay);
        populate(); // Invokes the method createItem()
    }
 
    // This method is invoked, when user tap on the map
    @Override
    public boolean onTap(GeoPoint p, MapView map) {
 
    	Log.i("K", "tapped");
    	
        // List<Overlay> overlays = map.getOverlays();
 
        // Creating a Message object to send to Handler
        Message message = new Message();
 
        // Creating a Bundle object ot set in Message object
        Bundle data = new Bundle();
 
        // Setting latitude in Bundle object
        data.putDouble("latitude", p.getLatitudeE6() / 1E6);
 
        // Setting longitude in the Bundle object
        data.putDouble("longitude", p.getLongitudeE6() / 1E6);
        
        // Setting type
        data.putBoolean("origin", isOrigin);
 
        // Setting the Bundle object in the Message object
        message.setData(data);
 
        // Sending Message object to handler
        handler.sendMessage(message);
 
        return super.onTap(p, map);
    }
}