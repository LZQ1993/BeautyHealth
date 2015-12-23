
package com.beautyhealth.UserCenter.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.HttpUtil;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UpdatePasswordActivity extends DataRequestActivity implements OnClickListener{
	private Button btn_updatepassword;
	private Button btn_sendregistercode;
	private Button btn_registercodeupdate;
	private EditText et_oldpassword;
	private EditText et_newpassword;
	private EditText et_passwordok;
	
	private String PasswordType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updatepassword);
		initNavBar("密码修改", "<返回", null);
		setAction(null);// user this------
		IsLocal = false;// user this------
		fetchUIFromLayout();
		setListener();
	}

	private void fetchUIFromLayout() {
		btn_updatepassword=(Button) findViewById(R.id.btn_updatepassword);
		btn_sendregistercode=(Button) findViewById(R.id.btn_sendregistercode);
		btn_registercodeupdate=(Button) findViewById(R.id.btn_registercodeupdate);
		et_oldpassword=(EditText) findViewById(R.id.et_oldpassword);
		et_newpassword=(EditText) findViewById(R.id.et_newpassword);
		et_passwordok=(EditText) findViewById(R.id.et_passwordok);
	}

	private void setListener() {
		btn_updatepassword.setOnClickListener(this);
		btn_sendregistercode.setOnClickListener(this);
		btn_registercodeupdate.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		//密码修改
		case R.id.btn_updatepassword:
			PasswordType = "2";
			isEmpty();
			
			
			break;
		//发送验证码
		case R.id.btn_sendregistercode:
			getUUID();
			break;
		//验证码修改密码
		case R.id.btn_registercodeupdate:
			PasswordType="1";
			isEmpty();
		
			break;

		default:
			break;
		}
	}
	
	private void isEmpty() {
		if(et_oldpassword.getText().toString().equals("")||et_passwordok.getText().toString().equals("")||et_newpassword.getText().toString().equals("")){
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
			updatePasswordTask();
		}
	}

	private void updatePasswordTask() {
		
		registeBroadcast();
		showProgressDialog("数据上传中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("UserManagerService", "passwordUpdate");
		Map requestCondition = new HashMap();
		String condition[] = {"UserID","OldPassword","NewPassword","PasswordType"};		
		ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage=(UserMessage) list.get(0);
		String value[] = {userMessage.UserID,et_oldpassword.getText().toString(),et_passwordok.getText().toString(),PasswordType};
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
	    this.requestData();
	}

	private void getUUID() {			
        String url = NetworkSetInfo.getServiceUrl() + "/UserManagerService/returnCode";        
        RequestParams params = new RequestParams();
        String condition[] = {"UserID"};
        ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage=(UserMessage) list.get(0);
		String value[] = {userMessage.UserID};			
		String strJson = JsonDecode.toJson(condition, value);
        params.put("json",strJson);
        HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
                 new AlertDialog.Builder(UpdatePasswordActivity.this)
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
							Toast.makeText(getApplicationContext(),msg.tip, Toast.LENGTH_SHORT).show();	
						}else{
							Toast.makeText(getApplicationContext(),msg.tip+"，请重新发送", Toast.LENGTH_SHORT).show();
						}
					}
				}
			}
        	
        });
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
				if (msg.getResult().equals("1")) {
					new AlertDialog.Builder(this)
					.setTitle("成功")
					.setMessage(msg.tip)
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									et_oldpassword.setText("");
									et_passwordok.setText("");
									et_newpassword.setText("");
									return;
								}
							})
					.setCancelable(false).show();
					return;
				}else{
					new AlertDialog.Builder(this)
					.setTitle("失败")
					.setMessage("更新失败")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									return;
								}
							})
					.setCancelable(false).show();
				}
			}else{
				new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("暂无数据")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								et_oldpassword.setText("");
								et_passwordok.setText("");
								et_newpassword.setText("");
								return;
							}
						})
				.setCancelable(false).show();
			}			
		
			}
		}
}
