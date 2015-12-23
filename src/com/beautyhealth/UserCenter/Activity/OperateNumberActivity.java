package com.beautyhealth.UserCenter.Activity;


import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWMobileDevice.AbsMobilePhone;
import com.beautyhealth.Infrastructure.CWMobileDevice.MobilePhone422;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class OperateNumberActivity extends NavAndTabBarActivity implements OnClickListener{
	private ImageButton btn_sendMessage;
	private ImageButton btn_callPhone;
	private ImageButton btn_updateNumber;
	private String phone;
	private String name;
	private String address;
	private String indexmy;
	private String message[]={"短信1","短信2","短信3","短信4","短信5","短信6","短信7","短信8"};
	AbsMobilePhone mobilePhone=new MobilePhone422();
//	private List<Object> user;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_operatenumber);
		fetchUIFromLayout();
		setListener();
		initNavBar("号码操作", "<返回", null);
		//获取FamilyNumberActivity中的信息 name,phone,address;在将接收到的消息传到相应的界面
//		Intent intent=getIntent();
//		Bundle bundle=intent.getExtras();
//		if(bundle!=null){
//			Toast.makeText(getApplicationContext(), message.size()+"", Toast.LENGTH_SHORT).show();
//			message=bundle.getStringArrayList("key");
//		}else{
//			Intent intentBreak=new Intent(getApplicationContext(), FamilyNumberActivity.class);
//			startActivity(intentBreak);
//		}
		
	}
	
	
	
	private void fetchUIFromLayout(){
		btn_sendMessage=(ImageButton) findViewById(R.id.btn_sendMessage);
		btn_callPhone=(ImageButton) findViewById(R.id.btn_callPhone);
		btn_updateNumber=(ImageButton) findViewById(R.id.btn_updateNumber);
	}
	
	
	
	private void setListener(){
		btn_sendMessage.setOnClickListener((OnClickListener)this);
		btn_callPhone.setOnClickListener((OnClickListener)this);
		btn_updateNumber.setOnClickListener((OnClickListener)this);
	}
	
	
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.btn_sendMessage:
			Intent intent=getIntent();
			Bundle bundle=intent.getExtras();
			//发短信 ，跳转到相应的界面
			if(bundle!=null){
				
				phone=bundle.getStringArrayList("key").get(2);
//				Toast.makeText(getApplicationContext(), message.size()+""+phone+"heheheheheh", Toast.LENGTH_SHORT).show();
//				Intent intent1=new Intent(OperateNumberActivity.this,SendMessageActivity.class);
//				Bundle b=new Bundle();
//				bundle.putString("phone", phone);
//				intent1.putExtras(bundle);
//				startActivity(intent1);
				//点击出现列表框，让用户进行选择
				new AlertDialog.Builder(this)
				.setTitle("选择要发送的消息：")
				.setItems(message, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						
						switch (arg1) {
						case 0:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[0] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;
						case 1:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[1] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						case 2:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[2] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						case 3:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[3] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						case 4:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[4] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						case 5:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[5] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						case 6:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[6] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						case 7:
							mobilePhone.SMSSend(OperateNumberActivity.this, phone,message[7] );
							Toast.makeText(getApplicationContext(), "发送成功！", Toast.LENGTH_SHORT).show();
							break;

						}
					}
				})
				.setNegativeButton("取消", null)
				.show();
				
				
				
			}else{
				Intent intentBreak=new Intent(getApplicationContext(), FamilyNumberActivity.class);
				startActivity(intentBreak);
			}
			break;
		case R.id.btn_callPhone:
			Intent intentcp=getIntent();
			Bundle bundlecp=intentcp.getExtras();
			phone=bundlecp.getStringArrayList("key").get(2);
//			Toast.makeText(getApplicationContext(), phone, Toast.LENGTH_SHORT).show();
			//打电话
			/*AbsMobilePhone mobilePhone=new MobilePhone422();
			mobilePhone.DialToNumber(phone);	*/	
			Intent _intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
					+ phone));
			startActivity(_intent);
			break;
			
		//修改亲情号码	
		case R.id.btn_updateNumber:
			Intent intentun=getIntent();
			Bundle bundleun=intentun.getExtras();
			indexmy=bundleun.getStringArrayList("key").get(0);
			name=bundleun.getStringArrayList("key").get(1);
			phone=bundleun.getStringArrayList("key").get(2);
			address=bundleun.getStringArrayList("key").get(3);
//			Toast.makeText(getApplicationContext(),"88888888"+indexmy+name+phone+address, Toast.LENGTH_SHORT).show();
			Intent intent3=new Intent(OperateNumberActivity.this,AddFamilyNumberActivity.class);
			Bundle b3=new Bundle();
			b3.putString("indexmy", indexmy);
			b3.putString("name", name);
			b3.putString("phone", phone);
			b3.putString("address", address);
			intent3.putExtras(b3);
			startActivity(intent3);
			break;

		default:
			break;
		}
	}
	
	

}













