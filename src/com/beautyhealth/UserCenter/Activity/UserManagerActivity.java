package com.beautyhealth.UserCenter.Activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.UserCenter.Activity.LoginActivity;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class UserManagerActivity extends NavAndTabBarActivity implements
		OnClickListener {

	private Button btn1, btn2;
	private TextView userid;
	private ISqlHelper iSqlHelper;
	private List<Object> list ;
	private UserMessage userMessage;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_usermanager);
		initNavBar("账户管理", "<返回", null);
		init();
		setListener();
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		iSqlHelper = new SqliteHelper(null,
				getApplicationContext());
	    list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
	    if(list.size()>0){
	    	userMessage=(UserMessage) list.get(0);
		    userid.setText(userMessage.UserID);
	    }
	    
	}


	private void init() {
		btn1 = (Button) findViewById(R.id.change);
		btn2 = (Button) findViewById(R.id.logout);
		userid=(TextView)findViewById(R.id.userid);

	}

	private void setListener() {
		// ll1.setOnClickListener((OnClickListener) this);
		btn1.setOnClickListener((OnClickListener) this);
		btn2.setOnClickListener((OnClickListener) this);

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.change:
			intent.setClass(getApplicationContext(), LoginActivity.class);
			intent.putExtra("goto", UserManagerActivity.class.getName());
			startActivity(intent);
			break;
		case R.id.logout:
			iSqlHelper = new SqliteHelper(null,
					getApplicationContext());
			list = iSqlHelper.Query(
					"com.beautyhealth.UserCenter.Entity.UserMessage", null);
			if(list.size()>0){
				userMessage = (UserMessage) list.get(0);
				iSqlHelper.SQLExec("delete from UserMessage where UserID = '"
						+ userMessage.UserID + "'");
				userid.setText("无账号");
			}else {
				ToastUtil.show(getApplicationContext(), "您处于未登录状态");
			}
			break;
		default:
			ToastUtil.show(getApplicationContext(), "输入有误!");
		}

	}

}
