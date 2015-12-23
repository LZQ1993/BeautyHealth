package com.beautyhealth.Infrastructure.CWDataRequest;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.beautyhealth.Infrastructure.CWFileSystem.AbsFileSystem;
import com.beautyhealth.Infrastructure.CWFileSystem.AssetFileSystem;

@SuppressWarnings("unused")
public class LocalRequest implements IRequest {
	private Context context;
	private String responseResult;

	public LocalRequest(Context context) {
		this.context = context;
	}

	public String readFileFromAssets(String filename) {
		AbsFileSystem mabs=new AssetFileSystem(context);
		return mabs.readTxt(filename);
	}

	@Override
	public String responseData() {

		return responseResult;
	}
	
	public void requestData(RequestUtility _requestUtility){
		responseResult = readFileFromAssets(_requestUtility.getMethodName()+".txt");
		Intent intent = new Intent();
		String action=context.getResources().getString(context.getResources().getIdentifier("datareload_action", "string",context.getPackageName()));
		intent.setAction(action);
		intent.putExtra("result", responseResult);
		
		
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
		//context.sendBroadcast(intent);
		//Log.e("aaaaaaaaaa", responseResult);
	}

}
