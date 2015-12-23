package com.beautyhealth.Infrastructure.CWComponent;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ResultBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		  String action = intent.getAction();
		  String actionReload=  context.getResources().getString(context.getResources().getIdentifier("datareload_action", "string",context.getPackageName()));
		  String errorAction=context.getResources().getString(context.getResources().getIdentifier("error_action", "string",context.getPackageName()));
		  if(action.equals(actionReload)) {
			  dataReload(context,intent);
		  }
		  else if(action.equals(errorAction)){
			  errorDataReload(context,intent);
		  }
	}
	public void dataReload(Context context, Intent intent){
		  Toast.makeText(context, "消息内容为:网络访问成功"+intent.getStringExtra("result"),
				Toast.LENGTH_SHORT).show();
	}
	public void errorDataReload(Context context, Intent intent){
		Toast.makeText(context, "消息内容为:"+intent.getStringExtra("result"),
				Toast.LENGTH_SHORT).show();
	}

}
