package com.beautyhealth.Infrastructure.CWDataRequest;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beautyhealth.Infrastructure.CWComponent.ResultBroadcastReceiver;
import com.beautyhealth.Infrastructure.CWDataDecode.DataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.IDataDecode;

public class DataRequestFragment extends Fragment {

	public String ClassFullName = null;
    public boolean IsLocal = false;
    public IDataDecode dataDecode=null;	
    public Object dataResult = null;
    private Context cxt;
    private String result = null;
    private LocalBroadcastManager broadcastFragment;
    
	private String errorAction;
    private String action = null;
	private boolean hasregister=false; 
	private RequestUtility requestUtility=null;
	
	private ResultBroadcastReceiver mydatareceive = new ResultBroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			super.onReceive(context, intent);	 
		}

		@Override
		public void dataReload(Context context, Intent intent) {
			result = intent.getStringExtra("result");
			updateView();
		}
		@Override
		public void errorDataReload(Context context, Intent intent) {
			errorTip();
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		//View _view =
		cxt=getActivity();
		if(dataDecode==null){
			dataDecode=new DataDecode(cxt);
			dataResult = new Object();
		}
		broadcastFragment=LocalBroadcastManager.getInstance(cxt);
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	public void setDataDecoder(IDataDecode _dataDecode)//,Object _dataResult)
	{
		dataDecode = _dataDecode;
		
	}
	
	public void setAction(String _action){
		if(_action==null){
			action=  getResources().getString(getResources().getIdentifier("datareload_action", "string",cxt.getPackageName()));
		}else{
			action=_action;
		}
	}
	public void setRequestUtility(RequestUtility _requestUtility){
		requestUtility=_requestUtility;	
	}
	
	private void registeBroadcast() {
		//ע����չ㲥  
		if(!hasregister){
			if(action==null){
				action=  getResources().getString(getResources().getIdentifier("datareload_action", "string",cxt.getPackageName()));
			}
			errorAction = getResources().getString(
					getResources().getIdentifier("error_action", "string",
							cxt.getPackageName()));
			IntentFilter filterStart = new IntentFilter(action);
			IntentFilter filterEnd = new IntentFilter(errorAction);
			broadcastFragment.registerReceiver(mydatareceive, filterStart);
			broadcastFragment.registerReceiver(mydatareceive, filterEnd);
	        hasregister=true;
		}
	}
	private void removeBroadcast() {
		if(hasregister){    
			broadcastFragment.unregisterReceiver(mydatareceive);
	        hasregister=false;
		}
	}

	//-------override
	public void errorTip(){
		////
	}
	
	//-------override
	public void updateView() {
		if (result != null) {
			dataResult=dataDecode.decode(result,ClassFullName);
		}
	}

	public void requestData(){
	
		if (IsLocal) {
			LocalRequest localRequest = new LocalRequest(cxt);
			localRequest.requestData(requestUtility);
		} else {
			NetRequest netRequest = new NetRequest(cxt);
			netRequest.requestData(requestUtility);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		removeBroadcast();
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		registeBroadcast();	
	}

}

