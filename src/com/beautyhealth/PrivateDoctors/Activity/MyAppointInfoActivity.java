package com.beautyhealth.PrivateDoctors.Activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ListView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PrivateDoctors.Assistant.AppointInfoAdapter;
import com.beautyhealth.PrivateDoctors.Entity.AppointInfo;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class MyAppointInfoActivity extends DataRequestActivity {
	private List<AppointInfo> appointInfo;
	private ListView lv_appointInfo;
	private AppointInfoAdapter appointInfoAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appoint_info);
		setAction(null);// user this------
		IsLocal = false;// user this------
		initNavBar("我的预约记录", "<返回", null);
		initList();
		fetchUIFromLayout();
		// setListener();
		initCommentData();
	}
	@Override
	public void onNavBarLeftButtonClick(View view) {
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(MyAppointInfoActivity.this,
				SearchHospitalActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
	}

	private void initCommentData() {
		registeBroadcast();
		showProgressDialog("数据加载中...");
		// user this------
		ClassFullName = "com.beautyhealth.PrivateDoctors.Entity.AppointInfo";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("HandyDoctorService", "searchAppoint");
		Map requestCondition = new HashMap();
		SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM");
		String Time = sDateFormat.format(new java.util.Date());
		String stime = Time + "-01 " + "00:00:00";
		String etime = Time + "-31 " + "23:59:59";
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage = (UserMessage) list.get(0);
		String UserID = userMessage.UserID;
		String condition[] = { "UserID","StartTime", "EndTime", "page", "rows" };
		String value[] = {UserID,stime, etime, "-1", "18" };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}

	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
				appointInfo.clear();
				for (int i = 0; i < realData.getResult().size(); i++) {
					AppointInfo appointInfoitem = (AppointInfo) realData
							.getResult().get(i);
					appointInfo.add(appointInfoitem);
				}
				hander.sendEmptyMessage(0);
			} else {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("暂无数据！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).setCancelable(false).show();
				return;
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle("错误")
					.setMessage("无数据信息！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							}).setCancelable(false).show();
		}
	}

	private void initList() {
		appointInfo = new ArrayList<AppointInfo>();
	}

	private void fetchUIFromLayout() {
		lv_appointInfo = (ListView) findViewById(R.id.lv_appointInfo);
		lv_appointInfo.setEmptyView(findViewById(R.id.empty_view));
	}

	private void setListener() {
		appointInfoAdapter = null;
		appointInfoAdapter = new AppointInfoAdapter(this, appointInfo);
		lv_appointInfo.setAdapter(appointInfoAdapter);
	}

	private Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			/*
			 * appointInfoAdapter.updateList(appointInfo);
			 * appointInfoAdapter.notifyDataSetChanged();
			 */
			setListener();
		}
	};

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.setClass(MyAppointInfoActivity.this,
				SearchHospitalActivity.class);
		startActivity(intent);
		overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
	}
	
	
	
}
