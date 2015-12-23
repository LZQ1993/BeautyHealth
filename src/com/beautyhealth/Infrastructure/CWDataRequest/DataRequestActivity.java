package com.beautyhealth.Infrastructure.CWDataRequest;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;

import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWComponent.ResultBroadcastReceiver;
import com.beautyhealth.Infrastructure.CWDataDecode.DataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.IDataDecode;

public class DataRequestActivity extends NavAndTabBarActivity {
	private ProgressDialog progressDialog;
	public String ClassFullName = null;
	public boolean IsLocal = true;
	public IDataDecode dataDecode = null;
	public Object dataResult = null;// ////////////

	private String result = null;

	private LocalBroadcastManager broadcastFragment;
	private String errorAction;
	private String action = null;
	public boolean hasregister = false;
	private RequestUtility requestUtility = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (dataDecode == null) {
			dataDecode = new DataDecode(this);
			dataResult = new Object();
		}
		broadcastFragment = LocalBroadcastManager
				.getInstance(getApplicationContext());
		registeBroadcast();

	}

	public void setDataDecoder(IDataDecode _dataDecode)// ,Object _dataResult)
	{
		dataDecode = _dataDecode;
		// dataResult = _dataResult;

	}

	public void setAction(String _action) {
		if (_action == null) {
			action = getResources().getString(
					getResources().getIdentifier("datareload_action", "string",
							getPackageName()));
		} else {
			action = _action;
		}
	}

	public void setRequestUtility(RequestUtility _requestUtility) {
		requestUtility = _requestUtility;
	}

	public void registeBroadcast() {
		if (!hasregister) {
			if (action == null) {
				action = getResources().getString(
						getResources().getIdentifier("datareload_action",
								"string", getPackageName()));
			}
			errorAction = getResources().getString(
					getResources().getIdentifier("error_action", "string",
							getPackageName()));
			IntentFilter filterStart = new IntentFilter(action);
			IntentFilter filterEnd = new IntentFilter(errorAction);
			broadcastFragment.registerReceiver(mydatareceive, filterStart);
			broadcastFragment.registerReceiver(mydatareceive, filterEnd);
			hasregister = true;
		}
	}

	private void removeBroadcast() {
		if (hasregister) {
			broadcastFragment.unregisterReceiver(mydatareceive);
			hasregister = false;
		}
	}

	private ResultBroadcastReceiver mydatareceive = new ResultBroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.e("ccccccccccc", intent.getAction());
			super.onReceive(context, intent);
			// Log.e("ddddddddd", intent.getAction());
		}

		@Override
		public void dataReload(Context context, Intent intent) {
			result = intent.getStringExtra("result");
			// Log.e("ddddddddd", intent.getAction());
			updateView();
			// Log.e("ddddddddd", intent.getAction());
		}

		@Override
		public void errorDataReload(Context context, Intent intent) {
			dismissProgressDialog();
			errorTip(intent.getStringExtra("error"));
		}
	};

	// -------override
	public void errorTip(String tip) {
		new AlertDialog.Builder(this).setTitle("错误").setMessage("网络数据获取失败,请检查网络后重试！")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).setCancelable(false).show();
	}

	// -------override
	public void updateView() {
		if (result != null) {
			dataResult = dataDecode.decode(result, ClassFullName);
		}
	}

	public void requestData() {

		if (IsLocal) {
			LocalRequest localRequest = new LocalRequest(
					getApplicationContext());
			localRequest.requestData(requestUtility);
		} else {
			NetRequest netRequest = new NetRequest(getApplicationContext());
			netRequest.requestData(requestUtility);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeBroadcast();
	}

	@Override
	protected void onStart() {
		super.onStart();
		registeBroadcast();
	}

	/**
	 * 显示进度条
	 */
	protected void showProgressDialog(String content) {
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage(content);
		progressDialog.setCancelable(false);
		progressDialog.show();
		progressDialog.setOnKeyListener(onKeyListener);

	}

	private OnKeyListener onKeyListener = new OnKeyListener() {
		@Override
		public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
			if (keyCode == KeyEvent.KEYCODE_BACK
					&& event.getAction() == KeyEvent.ACTION_DOWN) {
				removeBroadcast();
				dismissProgressDialog();
			}
			return false;
		}
	};

	/**
	 * 隐藏进度条
	 */
	protected void dismissProgressDialog() {
		if (isFinishing()) {
			removeBroadcast();
			return;
		}
		if (null != progressDialog && progressDialog.isShowing()) {
			removeBroadcast();
			progressDialog.dismiss();

		}
	}

/*	@Override
	public void onBackPressed() {
		if (progressDialog != null && progressDialog.isShowing()) {
			removeBroadcast();
			dismissProgressDialog();
		} else {
			removeBroadcast();
			super.onBackPressed();
		}
	}*/

	@Override
	protected void onResume() {
		registeBroadcast();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		removeBroadcast();
		super.onDestroy();
	}
	
	
}
