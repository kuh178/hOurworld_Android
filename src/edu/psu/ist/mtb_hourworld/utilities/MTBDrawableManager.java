package edu.psu.ist.mtb_hourworld.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.HashMap;

import edu.psu.ist.mtb_hourworld.R;
import edu.psu.ist.mtb_hourworld.constants.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
//import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class MTBDrawableManager {
	
	private static final int COMPLETE = 0;
	private static final int IN_COMPLETE = 1;
	private int mDimension;
	
	
	private ProgressBar mSpinner;
	private ImageView mImageView;
	private String mUrl;
	private File cacheDir;
	private final HashMap<String, Bitmap> drawableMap;
	private final Handler handler = new Handler() {
		public void handleMessage(Message message) {
			
			switch(message.what) {
			case COMPLETE:
				//mImageView.setImageDrawable((Drawable) message.obj);
				//mImageView.setImageBitmap();
				scaleImage((Bitmap) message.obj);
				mImageView.setVisibility(View.VISIBLE);
				mSpinner.setVisibility(View.GONE);
				break;
			case IN_COMPLETE:
				mImageView.setImageResource(R.drawable.hourworld_icon_30_30);
				mImageView.setVisibility(View.VISIBLE);
				mSpinner.setVisibility(View.GONE);
				break;
			}
		}
	};
	
	private void scaleImage(Bitmap bitmap)
	{
	    // Get the ImageView and its bitmap
	    //ImageView view = (ImageView) findViewById(R.id.image_box);
	    //Drawable drawing = mImageView.getDrawable();
	    //Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

	    // Get current dimensions AND the desired bounding box
	    int width = bitmap.getWidth();
	    int height = bitmap.getHeight();
	    int bounding = dpToPx(mDimension);

	    // Determine how much to scale: the dimension requiring less scaling is
	    // closer to the its side. This way the image always stays inside your
	    // bounding box AND either x/y axis touches it.  
	    float xScale = ((float) bounding) / width;
	    float yScale = ((float) bounding) / height;
	    float scale = (xScale <= yScale) ? xScale : yScale;

	    // Create a matrix for the scaling and add the scaling data
	    Matrix matrix = new Matrix();
	    matrix.postScale(scale, scale);

	    // Create a new bitmap and convert it to a format understood by the ImageView 
	    Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
	    width = scaledBitmap.getWidth(); // re-use
	    height = scaledBitmap.getHeight(); // re-use
	    BitmapDrawable result = new BitmapDrawable(scaledBitmap);

	    // Apply the scaled bitmap
	    mImageView.setImageDrawable(result);

	    // Now change ImageView's dimensions to match the scaled image
	    //LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mImageView.getLayoutParams(); 
	    //params.width = width;
	    //params.height = height;
	    //mImageView.setLayoutParams(params);

	}

	private int dpToPx(int dp)
	{
	    float density = mImageView.getContext().getApplicationContext().getResources().getDisplayMetrics().density;
	    return Math.round((float)dp * density);
	}
	
	public MTBDrawableManager(Context context, ProgressBar spinner, ImageView imageView) {
		mSpinner = spinner;
		mImageView = imageView;
		drawableMap = new HashMap<String, Bitmap>();
		
		mSpinner.setVisibility(View.VISIBLE);
		mImageView.setVisibility(View.GONE);
		
		//Find the dir to save cached images
        if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
            cacheDir=new File(Constants.MTB_CACHES);
        else {
        	cacheDir=context.getCacheDir();
        }

        if(!cacheDir.exists())
            cacheDir.mkdirs();
	}
	
	private Bitmap getBitmap(String url) 
	{
		//I identify images by hashcode. Not a perfect solution, good for the demo.
		String filename = String.valueOf(url.hashCode());
		File f = new File(cacheDir, filename);
		
		//from SD cache
		Bitmap b = decodeFile(f);
	       
		if(b != null)
			return b;
		//from web
		try {
			Bitmap bitmap = null;
			InputStream is = new URL(url).openStream();
			
			Log.i("K", is.toString());
			
			OutputStream os = new FileOutputStream(f);
			MTBListUtils.CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f);
			return bitmap;
			 
		} catch (Exception ex){
			ex.printStackTrace();
			return null;
		}
	}
	 
	 
	//decodes image and scales it to reduce memory consumption
	private Bitmap decodeFile(File f){
		
	    try {
	    	//decode image size
	    	BitmapFactory.Options option = new BitmapFactory.Options();
	    	option.inJustDecodeBounds = true;
	    	BitmapFactory.decodeStream(new FileInputStream(f),null, option);
	    	//Find the correct scale value. It should be the power of 2.
	            
	    	final int REQUIRED_SIZE = 200;
	            
	    	int width_tmp = option.outWidth; 
	    	int height_tmp = option.outHeight;
	    	int scale = 1;
	    	while(true){
	    		if(width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
	    			break;
	    		width_tmp /= 2;
	    		height_tmp /= 2;
	    		scale *= 2;
	    	}
	    	//decode with inSampleSize
	            
	    	BitmapFactory.Options option2 = new BitmapFactory.Options();
	            
	    	option2.inSampleSize = scale;
	            
	    	return BitmapFactory.decodeStream(new FileInputStream(f), null, option2);
	        
	    } catch (FileNotFoundException e) {}

	    return null;
	}
	
	public synchronized void fetchDrawableOnThread(String url, int dimension) {
		
		mDimension = dimension;
		
		mUrl = url;
	
		Thread thread = new Thread() {
			@Override
			public void run() {
				
				Bitmap bitmap = getBitmap(mUrl);
				Message message = null;
				
				if(bitmap == null) {
					message = handler.obtainMessage(IN_COMPLETE, bitmap);
				}
				else {
					message = handler.obtainMessage(COMPLETE, bitmap);
				}
				handler.sendMessage(message);
			}
		};
		
		thread.start();
	}
}
