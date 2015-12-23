package com.beautyhealth.UserCenter.Activity;



import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.UserCenter.Entity.FamilyNumberMessage;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class AddFamilyNumberActivity extends NavAndTabBarActivity{
	private EditText et_name;
	private EditText et_phone;
	private EditText et_address;
	private Bundle bundle;
	private String name;
	private String phone;
	private String address;
	private String indexmy;
	private String UserID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addfamilynumber);
		fetchUIFromLayout();
		initNavBar("亲情号设置","<返回", "保存");
		Intent intent=getIntent();
		bundle=intent.getExtras();
		indexmy=bundle.getString("indexmy");
		ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		List<Object> list=iSqlHelper.Query("com.beautyhealth.UserCenter.Entity.UserMessage", null);
		UserMessage userMessage=(UserMessage) list.get(0);
		UserID = userMessage.UserID;
		if(bundle.getString("name")!=null){
		et_name.setText(bundle.getString("name"));
		et_phone.setText(bundle.getString("phone"));
		et_address.setText(bundle.getString("address"));
		}
	}
	
	private void fetchUIFromLayout(){
		et_name=(EditText) findViewById(R.id.et_name);
		et_phone=(EditText) findViewById(R.id.et_phone);
		et_address=(EditText) findViewById(R.id.et_address);
	}
	
	private void valueReturn() {//返回数据
			//在AddFamilyNumber和FamilyNumberActivity之间传递信息，即修改“未指定联系人点击添加”为联系人信息
			//将数据放入bundle中在放入intent中
			Bundle bundlev=new Bundle();
			String str = "";
			if(et_name.getText().toString()!=null&&et_phone.getText().toString()!=null&&et_address.getText().toString()!=null){
				 str = et_name.getText().toString()+"\n"+et_phone.getText().toString()+"\n"+et_address.getText().toString();
			 }
			
			else{
				//将未指定联系人点击添加传回去，以免为空
				 str = str+getApplicationContext().getString(R.string.family_unadded);
			}
			
			bundlev.putString("ret", str); 
			
			Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
			intent.putExtras(bundlev);
			
			
			AddFamilyNumberActivity.this.setResult(RESULT_OK, intent);
			AddFamilyNumberActivity.this.finish();
			
	}

	 /**
     * 左按钮监听函数,返回
     */
    public void onNavBarLeftButtonClick(View view) {
    	Bundle bundle=new Bundle();
    	String  str = "未指定联系人点击添加";
		bundle.putString("ret", str); 
		Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
		intent.putExtras(bundle);
		AddFamilyNumberActivity.this.setResult(RESULT_OK, intent);
		AddFamilyNumberActivity.this.finish();
    }
	
    /**
     * 右按钮监听函数,保存
     */
	 public void onNavBarRightButtonClick(View view) {
		if((et_name.getText().toString().equals("")||et_name.getText().toString()==null)
				||(et_phone.getText().toString().equals("")||et_phone.getText().toString()==null)
				||(et_address.getText().toString().equals("")||et_address.getText().toString()==null)){
			new AlertDialog.Builder(this)
			.setTitle("提示")
			.setMessage("参数不能为空")
			.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							return;
						}
					}).setCancelable(false).show();

		}else{		 
		 ISqlHelper iSqlHelper=new SqliteHelper(null, getApplicationContext());
		 FamilyNumberMessage familyNumberMessage=new FamilyNumberMessage();
		 //如果数据不为空，则将数据添加到本地内存	 
		 if(bundle.getString("name")!=null){
			 familyNumberMessage.Indexmy=indexmy;
			 name =et_name.getText().toString();
			 phone =et_phone.getText().toString();
			 address =et_address.getText().toString();
			 familyNumberMessage.PeopleName=name;
			 familyNumberMessage.Tel=phone;
			 familyNumberMessage.Address=address;
			 familyNumberMessage.UserID=UserID;
			 String sqlstr = "update FamilyNumberMessage set PeopleName='"+name+"',Tel='"+phone+"',Address='"+address+"'  where UserID = '"+UserID+"'"+"and Indexmy='"+indexmy+"'";
			 iSqlHelper.SQLExec(sqlstr);
			 Toast.makeText(getApplicationContext(), "修改成功！", Toast.LENGTH_SHORT).show();
			 Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
			 intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		     intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			 startActivity(intent);
		 }else{
			 if(et_name.getText().toString()!=null&&et_phone.getText().toString()!=null&&et_address.getText().toString()!=null){
				 familyNumberMessage.Indexmy=indexmy;
				 familyNumberMessage.PeopleName=et_name.getText().toString();
				 familyNumberMessage.Tel=et_phone.getText().toString();
				 familyNumberMessage.Address=et_address.getText().toString();
				 familyNumberMessage.UserID=UserID;
				 iSqlHelper.Insert(familyNumberMessage);
				 valueReturn();
		 		 }
			 	}
		 
		}
	   }
	
	 @Override
		public boolean onKeyDown(int keyCode, KeyEvent event)//主要是对这个函数的复写
		{
			// TODO Auto-generated method stub 
	 
			if(keyCode == KeyEvent.KEYCODE_BACK)
			{
				Bundle bundle=new Bundle();
		    	String  str = "未指定联系人点击添加";
				bundle.putString("ret", str); 
				Intent intent=new Intent(getApplicationContext(), FamilyNumberActivity.class);
				intent.putExtras(bundle);
				AddFamilyNumberActivity.this.setResult(RESULT_OK, intent);
				AddFamilyNumberActivity.this.finish();
				return true; 
			}
			return super.onKeyDown(keyCode, event);
		}
	
}













