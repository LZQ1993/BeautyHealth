package com.beautyhealth.UserCenter.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.IDataDecode;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.UserCenter.Activity.OperateNumberActivity;
import com.beautyhealth.UserCenter.Entity.BindingMessage;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class MessageBindingActivity extends DataRequestActivity implements OnClickListener{
	private EditText et_registercode;
	private Button btn_addbinding;
	private Button btn_cancelbinding;
	private Button btn_synchronousbinding;
	//注册
	private String userErrorAction;
	private String userErrorAction2;
	private String useraction = null;
	private String useraction2 = null;
	private boolean userhasregister = false;
	private boolean userhasregister2 = false;
	private LocalBroadcastManager userbroadcastFragment;
	private LocalBroadcastManager userbroadcastFragment2;
	private String userresult = null;
	private String userresult2 = null;
	public IDataDecode dataDecode1 = null;
	public IDataDecode dataDecode2 = null;
	public Object dataResult1 = null;
	public Object dataResult2 = null;
	private String UserID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_messagebinding);
		initNavBar("被监护人绑定", "<返回", null);
		setAction(null);// user this------
		IsLocal = false;// user this------如果是true则是本地存储
		
		fetchUIFromLayout();
		setListener();

	}

	private void setListener() {
		btn_addbinding.setOnClickListener(this);
		btn_cancelbinding.setOnClickListener(this);
		btn_synchronousbinding.setOnClickListener(this);
	}

	private void fetchUIFromLayout() {
		et_registercode=(EditText) findViewById(R.id.et_registercode);
		btn_addbinding=(Button) findViewById(R.id.btn_addbinding);
		btn_cancelbinding=(Button) findViewById(R.id.btn_cancelbinding);
		btn_synchronousbinding=(Button) findViewById(R.id.btn_synchronousbinding);
		ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage=(UserMessage) list.get(0);
		UserID = userMessage.UserID;
		
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_addbinding:
			isEmpty();
			break;
		//从本地删除
		case R.id.btn_cancelbinding:
			ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
			List<Object> ls =iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.BindingMessage", null);
			if(ls.size()>0){
			String []message = new String[ls.size()];
			final String [] UnderGuardUserIDTip = new String[ls.size()];
			for(int i=0;i<ls.size();i++){
				BindingMessage bm = (BindingMessage) ls.get(i);
				message[i] = bm.UserName;
				UnderGuardUserIDTip[i] = bm.UnderGuardUserID;
			}
			new AlertDialog.Builder(this)
			.setTitle("选择要取消绑定的姓名：")
			.setItems(message, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {

					bindCancelUnderGuargeUser(UnderGuardUserIDTip[arg1]);
					}
			})
			.setNegativeButton("取消", null)
			.show();
			}else{
				new AlertDialog.Builder(MessageBindingActivity.this)
                .setTitle("提示")
                .setMessage("无绑定数据")
                .setPositiveButton("确定",null)
                .show(); 
                return;
			}
			break;
		//在服务器端将绑定的信息同步到本地
		case R.id.btn_synchronousbinding:
			bindUpdateUnderGuargeUser();
			break;

		default:
			break;
		}
		
	}
	
	private void bindCancelUnderGuargeUser(final String tip) {
		 String url = NetworkSetInfo.getServiceUrl() + "/UserManagerService/bindCancelUnderGuargeUser";        
	        RequestParams params = new RequestParams();
	        String condition[] = {"UserID","UnderGuardUserID"};
			String value[] = {UserID,tip};
			String strJson = JsonDecode.toJson(condition, value);
	        params.put("json",strJson);
	        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(Throwable error) {
	                 new AlertDialog.Builder(MessageBindingActivity.this)
	                 .setTitle("错误")
	                 .setMessage("网络访问失败，请检查网络！"+error.toString())
	                 .setPositiveButton("确定",null)
	                 .show(); 
	                 return;
				}
				@Override
				public void onSuccess(String content) {
					dataResult = dataDecode.decode(content, "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage");
					if (dataResult != null) {
						// user this------
						DataResult realData = (DataResult) dataResult;
						if (realData.getResultcode().equals("1")) {
							ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData.getResult().get(0);
							if (msg.getResult().equals("1")) {	
								ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
								iSqlHelper.SQLExec("delete from BindingMessage where UnderGuardUserID = '"+tip+"'");
								new AlertDialog.Builder(MessageBindingActivity.this)
				                 .setTitle("成功")
				                 .setMessage(msg.tip)
				                 .setPositiveButton("确定",new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {									
											return;
										}
									}).setCancelable(false).show();
							}else{
								new AlertDialog.Builder(MessageBindingActivity.this)
				                 .setTitle("错误")
				                 .setMessage(msg.tip)
				                 .setPositiveButton("确定",null)
				                 .show(); 
				                 return;
							}
						}else{
							new AlertDialog.Builder(MessageBindingActivity.this)
			                 .setTitle("错误")
			                 .setMessage("暂无数据")
			                 .setPositiveButton("确定",null)
			                 .show(); 
			                 return;
						}
					}
				}
	        	
	        });
		}

	private void bindUpdateUnderGuargeUser() {
			String url = NetworkSetInfo.getServiceUrl() + "/UserManagerService/bindUpdateUnderGuargeUser";        
	        RequestParams params = new RequestParams();
	        String condition[] = {"UserID"};
			String value[] = {UserID};
			String strJson = JsonDecode.toJson(condition, value);
	        params.put("json",strJson);
	        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
				@Override
				public void onFailure(Throwable error) {
	                 new AlertDialog.Builder(MessageBindingActivity.this)
	                 .setTitle("错误")
	                 .setMessage("网络访问失败，请检查网络！"+error.toString())
	                 .setPositiveButton("确定",null)
	                 .show(); 
	                 return;
				}
				@Override
				public void onSuccess(String content) {
					dataResult = dataDecode.decode(content, "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage");
					if (dataResult != null) {
						// user this------
						DataResult realData = (DataResult) dataResult;
						if (realData.getResultcode().equals("1")) {
							for(int i=0;i<realData.getResult().size();i++){
								ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData.getResult().get(i);
								BindingMessage bindingMessage = new BindingMessage();
								bindingMessage.UnderGuardUserID=msg.result;
								bindingMessage.UserID = UserID;
								bindingMessage.UserName = msg.tip;
								ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());	
								List<Object> ls = iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.BindingMessage","UnderGuardUserID = '"+msg.result+"'");
								if(ls.size()==0){
									iSqlHelper.Insert(bindingMessage);
								}else{
									iSqlHelper.SQLExec("update BindingMessage set UserName = '"+msg.tip+"' where UnderGuardUserID = '"+msg.result+"'");
								}
						   }
							new AlertDialog.Builder(MessageBindingActivity.this)
							.setTitle("成功")
							.setMessage("绑定数据同步成功")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											return;
										}
									})
							.setCancelable(false).show();
							
						}else{
							new AlertDialog.Builder(MessageBindingActivity.this)
							.setTitle("错误")
							.setMessage("数据加载异常")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(DialogInterface dialog,
												int which) {
											et_registercode.setText("");
											return;
										}
									})
							.setCancelable(false).show();
						}
					}
				}
	        	
	        });
		}

	private void isEmpty() {
		if(et_registercode.getText().toString().equals("")){
			new AlertDialog.Builder(this)
			.setTitle("提示")
			.setMessage("请填写完整信息！")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							return;
						}
					})
			.setCancelable(false).show();
		}else{
			bindUnderGuargeUser();
		}
	}	
	private void bindUnderGuargeUser() {
		    registeBroadcast();
			showProgressDialog("数据上传中，请稍候...");
			// user this------
			ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
			// user this------
			RequestUtility myru = new RequestUtility(this);
			myru.setIP(null);
			myru.setMethod("UserManagerService", "bindUnderGuargeUser");
			Map requestCondition = new HashMap();
			String condition[] = {"UserID","UnderGuardUserID"};		
			String value[] = {UserID,et_registercode.getText().toString()};
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
					ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData.getResult().get(0);
					if (msg.getResult().equals("0")) {
						new AlertDialog.Builder(this)
						.setTitle("失败")
						.setMessage(msg.tip)
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								})
						.setCancelable(false).show();
					}else{
						BindingMessage bindingMessage = new BindingMessage();
						bindingMessage.UnderGuardUserID=msg.result;
						bindingMessage.UserID = UserID;
						bindingMessage.UserName = msg.tip;
						ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());	
						//BindingMessage bindingMessage = new BindingMessage();
						List<Object> ls = iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.BindingMessage","UnderGuardUserID = '"+msg.result+"'");
						if(ls.size()==0){
							iSqlHelper.Insert(bindingMessage);
						}else{
							iSqlHelper.SQLExec("update BindingMessage set UserName = '"+msg.tip+"' where UnderGuardUserID = '"+msg.result+"'");
						}
						new AlertDialog.Builder(this)
						.setTitle("成功")
						.setMessage("绑定成功")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										et_registercode.setText("");
										return;
									}
								})
						.setCancelable(false).show();
						return;
						
					}
				}else{
					
					new AlertDialog.Builder(this)
					.setTitle("错误")
					.setMessage("数据加载异常")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									et_registercode.setText("");
									return;
								}
							})
					.setCancelable(false).show();
				}			
			
				}
		}	
}
















