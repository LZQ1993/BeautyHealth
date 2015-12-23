package com.beautyhealth.Infrastructure.CWComponent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class CWMediaHelper {

	private Context cxt;
	public CWMediaHelper(Context _cxt){
		cxt=_cxt;
	}
	
	public Bitmap CreateBitmap(int resourceID,double scaleRate){
		Bitmap _bitmap=BitmapFactory.decodeResource(cxt.getResources(), resourceID);
		double bitmapWidth=((double)_bitmap.getWidth())*scaleRate;
		double bitmapHeight=((double)_bitmap.getHeight())*scaleRate;
		Bitmap _newmap=Bitmap.createScaledBitmap(_bitmap,(int)bitmapWidth,(int)bitmapHeight, true);
//		if(_bitmap!=null&&!_bitmap.isRecycled()){
//			_bitmap.recycle();
//		}
		return _newmap;
	}
}
