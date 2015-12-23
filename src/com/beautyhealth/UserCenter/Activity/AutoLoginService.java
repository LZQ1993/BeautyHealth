package com.beautyhealth.UserCenter.Activity;

import java.util.List;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.PrivateDoctors.Assistant.CommentAdapter;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.google.gson.Gson;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class AutoLoginService extends Service {

	
	private Intent intent1;
	private final Gson gson = new Gson();
	private LocalBroadcastManager localBroadcastManager;
	
	
	
	@Override
	public IBinder onBind(Intent intent) {
		
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		
		
		super.onCreate();
		Log.e("onCreate()", "自动登录");
		autologin();
		
		
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		
		
		return super.onStartCommand(intent, flags, startId);
		
	}

	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		

	}
	

	public class autologinServiceBinder extends Binder {

	}

	public void autologin() {
	
		String url = NetworkSetInfo.getServiceUrl()
				+ "/UserManagerService/loginUserManager";
		RequestParams params = new RequestParams();
		String condition[] = { "UserID", "Password", "PasswordType" };
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage = (UserMessage) list.get(0);
		String value[] = { userMessage.UserID, userMessage.Password,
				userMessage.PasswordType };
		String strJson = JsonDecode.toJson(condition, value);
		
		params.put("json", strJson);
		
		
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			
			@Override
			public void onFailure(Throwable error) {
				
				localBroadcastManager=LocalBroadcastManager.getInstance(getApplicationContext());
				intent1 = new Intent("com.beautyhealth.UserCenter.Activity.AutoLogin");
				intent1.putExtra("state", "Failure");
				localBroadcastManager.sendBroadcast(intent1);
				return;
			}

			@Override
			public void onSuccess(String content) {
				
				
				AutoDataResult dataResult = null;
				
				if (content != null) {
					dataResult=gson.fromJson(content, AutoDataResult.class);
				}
				if((dataResult.getResult().get(0)).getResult().equals("1")){
					
					
					localBroadcastManager=LocalBroadcastManager.getInstance(getApplicationContext());
					intent1 = new Intent("com.beautyhealth.UserCenter.Activity.AutoLogin");
					intent1.putExtra("state", "Success");
					localBroadcastManager.sendBroadcast(intent1);
				
					
				}
				/*else{
					localBroadcastManager=LocalBroadcastManager.getInstance(getApplicationContext());
					intent1 = new Intent("com.beauthyhealth.AutoLogin");
					intent1.putExtra("state", dataResult.getResult().get(0).getTip());
					localBroadcastManager.sendBroadcast(intent1);
					
				}*/

			}

		});
		
	}
	
	

}
