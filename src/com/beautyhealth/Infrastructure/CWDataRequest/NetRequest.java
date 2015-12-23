package com.beautyhealth.Infrastructure.CWDataRequest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.R;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class NetRequest implements IRequest {
	private String responseResult;
	private Context context;
	
	
	public NetRequest(Context context) {
		this.context = context;
	}
	
	public String getResponseResult() {
		return responseResult;
	}

	public void setResponseResult(String responseResult) {
		this.responseResult = responseResult;
	}

	@Override
	public String responseData() {

		return responseResult;
	}

	@Override
	public void requestData(RequestUtility _requestUtility) {
		 RequestParams params = new RequestParams();
		 Iterator iterator = _requestUtility.getParams().keySet().iterator();                
         while (iterator.hasNext()) {    
          Object key = iterator.next();  
          if(((String)key).startsWith("image")){
				try {
					params.put(key.toString(),
							(File) (_requestUtility.getParams().get(key)),
							"application/octet-stream");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
          }else{
        	  params.put(key.toString(),_requestUtility.getParams().get(key)); 
          }
         }  
			

		 final String action=_requestUtility.getNotificationName();
         HttpUtil.post(_requestUtility.getURLString(), params, new AsyncHttpResponseHandler() {
        	
        	Intent  intent = new Intent();
        	
			@Override
			public void onFailure(Throwable error) {
				responseResult = null;
				String _errorAction=context.getResources().getString(context.getResources().getIdentifier("error_action", "string",context.getPackageName()));
				intent.putExtra("error", error.toString());
				intent.setAction(_errorAction);
				//Log.e("aaaaaaaaaa", intent.getAction());
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				//Log.e("bbbbbbbbb", intent.getAction());
			}
			@Override
			public void onSuccess(String content) {
				responseResult = content;
				intent.setAction(action);
				intent.putExtra("result", responseResult);
				LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
				//Log.e("aaaaaaaaaa", content);
			}
         });
		
	}

}
