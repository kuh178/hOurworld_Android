package edu.psu.ist.mtb_hourworld.location;

import edu.psu.ist.mtb_hourworld.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BalloonOverlayView extends FrameLayout{

	private LinearLayout layout;
	private TextView description;

	/**
	 * Create a new BalloonOverlayView.
	 * 
	 * @param context - The activity context.
	 * @param balloonBottomOffset - The bottom padding (in pixels) to be applied
	 * when rendering this view.
	 */
	public BalloonOverlayView(Context context, int balloonBottomOffset, int previousActivity, int position) {

		super(context);
		
		setPadding(10, 0, 10, 30);
		layout = new LinearLayout(context);
		layout.setVisibility(VISIBLE);
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
		View v = inflater.inflate(R.layout.balloon_overlay, layout);
		
		description = (TextView) v.findViewById(R.id.balloon_item_description);
		
		// when click the close button
		ImageView close = (ImageView) v.findViewById(R.id.close_img_button);
		close.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				layout.setVisibility(GONE);
			}
		});
		
		
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.NO_GRAVITY;

		addView(layout, params);
	}
	/**
	 * Sets the view data from a given overlay item.
	 * 
	 * @param item - The overlay item containing the relevant view data 
	 * (title and snippet). 
	 */
	
	public void setData(final MobileTimeBankingOverlayItem item) {

		layout.setVisibility(VISIBLE);
		
		// show the description
		description.setText(item.getTitle());

	}
}

