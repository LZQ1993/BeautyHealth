package com.beautyhealth.UserCenter.Activity;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWMobileDevice.AbsMobilePhone;
import com.beautyhealth.Infrastructure.CWMobileDevice.MobilePhone422;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SendMessageActivity extends NavAndTabBarActivity implements OnClickListener{
	private EditText et_sendmessage;
	private Button btn_sendmessage;
	private String phone;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sendmessage);
		initNavBar("发短信", "<返回", null);
		fetchUIFromLayout();
		setListener();
		//获取OperateNumber号码操作界面获取的消息，发短信只需要电话号码
		Intent intent=getIntent();
		Bundle bundle=intent.getExtras();
		//获取到的电话号码
		phone= bundle.getString("phone");
		Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_SHORT).show();
		
	}
	private void fetchUIFromLayout(){
		et_sendmessage=(EditText) findViewById(R.id.et_sendmessage);
		btn_sendmessage=(Button) findViewById(R.id.btn_sendmessage);
	}
	
	
	//实现类似快速回复短信的样子
	private void setListener(){
		btn_sendmessage.setOnClickListener(this);
	}	
		
	
	@Override
	public void onClick(View arg0) {
		
		AbsMobilePhone mobilePhone=new MobilePhone422();
		mobilePhone.SMSSend(SendMessageActivity.this, phone, et_sendmessage.getText().toString());
		Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
	}
	
}








