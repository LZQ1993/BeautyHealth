package com.beautyhealth.UserCenter.Activity;

import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;


public class UserFeedBackActivity extends DataRequestActivity {

    private EditText edtBug;
    private String UserID = "13591995551";

    /**
     * onCreate
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initNavBar("问题反馈", "<返回", null);
		setAction(null);// user this------
		IsLocal = false;// user this------
		fetchUIFromLayout();
		setListener();    
    }
 
    private void initValue() {
    	edtBug.setText("");
		
	}

	private void setListener() {

		
	}

	private void fetchUIFromLayout() {
    	 edtBug = (EditText) findViewById(R.id.bug_feedback_edt_bug);
	}

	/**
     * 提交条件
     */
    public void onSubmit(View view) {
        if(this.edtBug.getText().toString().equals("")) {
            new AlertDialog.Builder(this)    
            .setTitle("提示")  
            .setMessage("亲，您还未填写任何反馈信息！")  
            .setPositiveButton("确定", null) 
            .show(); 
        } else {
        	upLoad();
        }
    }

	private void upLoad() {
		registeBroadcast();
		showProgressDialog("数据上传中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		// myru.setIP(NetworkInfo.getServiceUrl());
		myru.setIP(null);
		myru.setMethod("UserManagerService", "uploadUserSuggest");
		Map requestCondition = new HashMap();
		String condition[] = { "Description","UserID" };
		String value[] = { edtBug.getText().toString(), UserID };

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
							.setMessage(msg.getTip() + "请重新上传。")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											upLoad();
										}
									})
							.setNegativeButton("取消",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											initValue();
											return;
										}
									}).setCancelable(false).show();
				    	return;
				}
				if (msg.getResult().equals("1")) {
					new AlertDialog.Builder(this)
					.setTitle("提示")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									initValue();
									return;
								}
							}).setMessage(msg.getTip()+",您的反馈已提交，感谢您的支持！")
					.setCancelable(false).show();
					return;
				}
		    }else{
		    	
		    	new AlertDialog.Builder(this)
				.setTitle("提示")
				.setMessage("数据上传失败,点击确定重新上传。")
				.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								upLoad();
							}
						})
				.setNegativeButton("取消",
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(
									DialogInterface dialog,
									int which) {
								initValue();
								return;
							}
						}).setCancelable(false).show();
	    	return;
		    }	
		}
		}
}
