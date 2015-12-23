package com.beautyhealth.PersonHealth.MyLocation.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class GpsLocationReceiver extends BroadcastReceiver {

	Context context;
	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		if(intent.getAction().equals("com.chinawit.locationuploading")){   
		  /*
	      //ICWTask mytask=new LocationNetLoadTask();//可以在这里改变
	        ICWTask mytask=new LocalSqliteTask();
	        mytask.Execute(context); 
	        ToastUtil.show(context, "数据写入");
	        
	       */
			Intent _intent = new Intent(context,LocationService.class);
			context.startService(_intent);
			
		}
	       
	}
}
