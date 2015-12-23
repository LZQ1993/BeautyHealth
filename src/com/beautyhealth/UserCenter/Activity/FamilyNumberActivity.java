package com.beautyhealth.UserCenter.Activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.ResultBroadcastReceiver;
import com.beautyhealth.Infrastructure.CWDataDecode.DataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.IDataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.UserCenter.Entity.FamilyNumberMessage;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class FamilyNumberActivity extends DataRequestActivity implements OnClickListener{
	private Button btn_1;
	private Button btn_2;
	private Button btn_3;
	private Button btn_upload;
	private Button btn_download;
	private String str;
	private  List<Object> user;
	
	//注册通知时用
	private String userErrorAction;
	private String useraction = null;
	private boolean userhasregister = false;
	private LocalBroadcastManager userbroadcastFragment;
	private String userresult = null;
	public IDataDecode dataDecode1 = null;
	public Object dataResult1 = null;
	
	private String[] indexmys=new String[3];
	private String[] names=new String[3];
	private String[] phones=new String[3];
	private String[] addresses=new String[3];
    private String UserID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_familynumber);
		initNavBar("亲情号设置", "<返回", null);
//		注册个通知，上传和下载都要更新视图所以都要用updateview，加通知是为了知道是哪个用该方法。
		userbroadcastFragment = LocalBroadcastManager
				.getInstance(getApplicationContext());
		registeBroadcast1();
		
		setAction(null);// user this------
		IsLocal = false;// user this------如果是true则是本地存储
		fetchUIFromLayout();
		setListener();
	}
	
	
	//初始化控件
	private void fetchUIFromLayout(){
		btn_1=(Button) findViewById(R.id.btn_1);
		btn_2=(Button) findViewById(R.id.btn_2);
		btn_3=(Button) findViewById(R.id.btn_3);
		btn_upload=(Button) findViewById(R.id.btn_upload);
		btn_download=(Button) findViewById(R.id.btn_download);
		ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage=(UserMessage) list.get(0);
		UserID = userMessage.UserID;
	}
	
	//设置监听器	
	private void setListener(){
		btn_1.setOnClickListener(this);
		btn_2.setOnClickListener(this);
		btn_3.setOnClickListener(this);
		btn_upload.setOnClickListener(this);
		btn_download.setOnClickListener(this);
	}
	//按钮的点击事件
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_1:
				redirectIntent(1, btn_1);
				
				break;
			case R.id.btn_2:
				redirectIntent(2, btn_2);
				
				break;
			case R.id.btn_3:
				redirectIntent(3, btn_3);
				
				break;
			case R.id.btn_upload:
				upLoad();
				
				break;
			case R.id.btn_download:
				downLoad();
				
				break;

			default:
				break;
			}

		}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode==1){
			//接收AddFamilyNumber中传过来的数据
			Bundle b=data.getExtras();
			str=b.getString("ret");
			btn_1.setText(str);
			
			
		}
		if(requestCode==2){
			Bundle b=data.getExtras(); 
			str=b.getString("ret");
			btn_2.setText(str);
			
		}
		if(requestCode==3){
			//接收数据
			Bundle b=data.getExtras(); 
			str=b.getString("ret");
			btn_3.setText(str);
			
		}
		
	}
	private void redirectIntent(int flag, Button btn) {
		String familyUnadded=getApplicationContext().getString(R.string.family_unadded);
		if(btn.getText().toString().equals(familyUnadded)){
			Intent intent=new Intent(FamilyNumberActivity.this,AddFamilyNumberActivity.class);	
			Bundle bundle=new Bundle();
			bundle.putString("indexmy", String.valueOf(flag));
			ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
			List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);			
			UserMessage userMessage=(UserMessage) list.get(0);
			bundle.putString("UserID",userMessage.UserID);
			intent.putExtras(bundle);
			startActivityForResult(intent, flag);
		}
		else {
			
//			将从AddFamilyNumber中的消息传到号码操作界面OperateNumberActivity
//			FamilyNumberMessage famliyNumberMessage=(FamilyNumberMessage) user.get(flag-1);
			List<String> sendMessage=new ArrayList<String>();
			sendMessage.add(indexmys[flag-1]);
			sendMessage.add(names[flag-1]);
			sendMessage.add(phones[flag-1]);
			sendMessage.add(addresses[flag-1]);
			Intent intent=new Intent(FamilyNumberActivity.this,OperateNumberActivity.class);
			Bundle bundle=new Bundle();
			bundle.putStringArrayList("key", (ArrayList<String>) sendMessage);
			intent.putExtras(bundle);
			startActivity(intent);
			
			
		}
	}
	
	
	
//上传数据
	private void upLoad(){
		registeBroadcast();
		//进度条
		showProgressDialog("数据上传中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		// myru.setIP(NetworkInfo.getServiceUrl());
		myru.setIP(null);
		//传入接口名和方法名，到接口文档中查看
		myru.setMethod("FamilyService", "uploadFamily");
		Map requestCondition = new HashMap();
		String condition[] = { "Indexmy", "PeopleName", "Tel","Address","UserID" };
		//从本地中读取数据
		ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		 String whereString = " UserID = '"+UserID+"'";
		user =iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.FamilyNumberMessage", whereString);
		String strJson="";
		
		for(int i=0;i<user.size();i++){
			
			FamilyNumberMessage familyNumberMessage=(FamilyNumberMessage) user.get(i);
			
			String value[]={familyNumberMessage.Indexmy,familyNumberMessage.PeopleName,
			familyNumberMessage.Tel,familyNumberMessage.Address,UserID};
			if(i==user.size()-1){
				strJson=strJson+JsonDecode.toJson(condition, value);
			}
			else {
				strJson=strJson+JsonDecode.toJson(condition, value)+",";
			}
		}
		
		strJson = "{\"RealData\":["+strJson+"]}";
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
		this.requestData();
		
	}
	

	//注册标记
	private void registeBroadcast1() {
		if (!userhasregister) {

			useraction = "userdownloadAction";

			userErrorAction = getResources().getString(
					getResources().getIdentifier("error_action", "string",
							getPackageName()));
			IntentFilter filterStart = new IntentFilter(useraction);
			IntentFilter filterEnd = new IntentFilter(userErrorAction);
			userbroadcastFragment.registerReceiver(downUser, filterStart);
			userbroadcastFragment.registerReceiver(downUser, filterEnd);
			userhasregister = true;
		}
	}
	
	private ResultBroadcastReceiver downUser = new ResultBroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			
			 if(intent.getAction().equals(useraction)) {
				
				  dataReload1(context,intent);
			  }
			  else if(intent.getAction().equals(userErrorAction)){
				
				  errorDataReload1(context,intent);
			  }
		}
	};
		public void dataReload1(Context context, Intent intent) {
			
			indexmys=new String[3];
			names=new String[3];
			phones=new String[3];
			addresses=new String[3];
			
			userresult = intent.getStringExtra("result");
			dismissProgressDialog();
			if (userresult != null) {
				dataDecode1 = new DataDecode(context);
				dataResult1 = new Object();
				dataResult1 = dataDecode1.decode(userresult, ClassFullName);
			}
			//数据解析，具体显示
			if (dataResult1 != null) {
			
				DataResult realData1 = (DataResult) dataResult1;
				ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
				iSqlHelper.SQLExec("delete from FamilyNumberMessage where UserID='"+UserID+"'");// 删除表中原有的数据，保证只有一条
				//创建接收返回数据的表，
				btn_1.setText(getApplicationContext().getString(R.string.family_unadded));
				btn_2.setText(getApplicationContext().getString(R.string.family_unadded));
				btn_3.setText(getApplicationContext().getString(R.string.family_unadded));
			if (realData1.getResultcode().equals("1")) {
				// 从服务器读数据
				for (int i = 0; i < realData1.getResult().size(); i++) {
					FamilyNumberMessage msg = (FamilyNumberMessage) realData1.getResult().get(i);
					// 将读到的数据逐条插入到相应的表中
					iSqlHelper.Insert(msg);
					if (msg.Indexmy.equals("1")) {
						indexmys[0] = msg.Indexmy;
						names[0] = msg.PeopleName;
						phones[0] = msg.Tel;
						addresses[0] = msg.Address;
						btn_1.setText(msg.PeopleName + "\n" + msg.Tel + "\n"
								+ msg.Address);
					}else if (msg.Indexmy.equals("2")) {
						
						indexmys[1] = msg.Indexmy;
						names[1] = msg.PeopleName;
						phones[1] = msg.Tel;
						addresses[1] = msg.Address;
						btn_2.setText(msg.PeopleName + "\n" + msg.Tel + "\n"
								+ msg.Address);
					}else if (msg.Indexmy.equals("3")) {
						
						indexmys[2] = msg.Indexmy;
						names[2] = msg.PeopleName;
						phones[2] = msg.Tel;
						addresses[2] = msg.Address;
						btn_3.setText(msg.PeopleName + "\n" + msg.Tel + "\n"
								+ msg.Address);
					}
				}

			} else {
				new AlertDialog.Builder(FamilyNumberActivity.this)
						.setTitle("错误")
						.setMessage("暂无数据")
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
			}
			
		}
		public void errorDataReload1(Context context, Intent intent) {
			dismissProgressDialog();
			new AlertDialog.Builder(FamilyNumberActivity.this)
			.setTitle("错误")
			.setMessage("数据加载异常")
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

	private void removeBroadcast1() {
		if (userhasregister) {
			userbroadcastFragment.unregisterReceiver(downUser);
			userhasregister = false;
		}
	}
	
	
	//下载数据
	private void downLoad(){
		//进度条
		showProgressDialog("数据下载中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.UserCenter.Entity.FamilyNumberMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("FamilyService", "downloadFamily");//传入接口名和方法名，到接口文档中查看
		Map requestCondition = new HashMap();
		String condition[] = { "UserID", "page","rows"};
		String value[]={UserID,"1","3"};
		String strJson=JsonDecode.toJson(condition, value);
		requestCondition.put("Json", strJson);
		//设置通知
		myru.setNotification(useraction);	
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}
	
	
	
	//更新界面，上传至服务器后得到的结果。
	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
			ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData.getResult().get(0);
				if (msg.getResult().equals("0")) {
					new AlertDialog.Builder(this)
							.setTitle("失败")
							.setMessage(msg.getTip())
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											return;
										}
									}).setCancelable(false).show();
				} else if (msg.getResult().equals("1")) {
					new AlertDialog.Builder(this)
							.setTitle("提示")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											return;
										}
									}).setMessage(msg.getTip())
							.setCancelable(false).show();
					return;
				}
			} else {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("暂无数据")
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
			.setMessage("数据加载异常")
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
	}
	
	
	
	//从本地读取数据用来显示是联系人的信息还是“未指定联系人点击添加”
	 private void isEmpty(){		
		 ISqlHelper iSqlHelper= new SqliteHelper(null, getApplicationContext());
		 String whereString = "UserID='"+UserID+"'";
		 user =iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.FamilyNumberMessage", whereString);
		 if(user.size()>0){	 
			for (int i = 0; i < user.size(); i++) {
				FamilyNumberMessage famliyNumberMessage = (FamilyNumberMessage) user
						.get(i);
				if (famliyNumberMessage.Indexmy.equals("1")) {
					if (famliyNumberMessage.PeopleName != null
							&& famliyNumberMessage.Tel != null
							&& famliyNumberMessage.Address != null) {
						btn_1.setText(famliyNumberMessage.PeopleName + "\n"
								+ famliyNumberMessage.Tel + "\n"
								+ famliyNumberMessage.Address);
					} else {
						btn_1.setText(R.string.family_unadded);
					}
					indexmys[0] = famliyNumberMessage.Indexmy;
					names[0] = famliyNumberMessage.PeopleName;
					phones[0] = famliyNumberMessage.Tel;
					addresses[0] = famliyNumberMessage.Address;
				}
				if (famliyNumberMessage.Indexmy.equals("2")) {
					if (famliyNumberMessage.PeopleName != null
							&& famliyNumberMessage.Tel != null
							&& famliyNumberMessage.Address != null) {
						btn_2.setText(famliyNumberMessage.PeopleName + "\n"
								+ famliyNumberMessage.Tel + "\n"
								+ famliyNumberMessage.Address);
					} else {
						btn_2.setText(R.string.family_unadded);
					}
					indexmys[1] = famliyNumberMessage.Indexmy;
					names[1] = famliyNumberMessage.PeopleName;
					phones[1] = famliyNumberMessage.Tel;
					addresses[1] = famliyNumberMessage.Address;
				}
				if (famliyNumberMessage.Indexmy.equals("3")) {
					if (famliyNumberMessage.PeopleName != null
							&& famliyNumberMessage.Tel != null
							&& famliyNumberMessage.Address != null) {
						btn_3.setText(famliyNumberMessage.PeopleName + "\n"
								+ famliyNumberMessage.Tel + "\n"
								+ famliyNumberMessage.Address);
					} else {
						btn_3.setText(R.string.family_unadded);
					}
					indexmys[2] = famliyNumberMessage.Indexmy;
					names[2] = famliyNumberMessage.PeopleName;
					phones[2] = famliyNumberMessage.Tel;
					addresses[2] = famliyNumberMessage.Address;
				}
			}
		}
	 }
	 
	 
	 
	 @Override
		protected void onPause() {
			super.onPause();
			removeBroadcast1();

		}

		@Override
		protected void onStart() {
			super.onStart();
			registeBroadcast1();
	
		}
	
		@Override
		protected void onResume() {
			// TODO Auto-generated method stub
			super.onResume();
			isEmpty();
		}
		@Override
		protected void onStop() {
			super.onStop();
	
		}
	
}
