package com.beautyhealth.UserCenter.Activity;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
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
import com.beautyhealth.PrivateDoctors.Assistant.CommentAdapter;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class PersonalInformationManagerActivity extends DataRequestActivity
		implements OnClickListener {
	private Button btn_getregiatercode;
	private Button btn_updatepassword;
	private TextView tv_phone;
	private EditText et_registercode;
	private EditText et_name;
	private EditText et_birthday;
	private EditText et_tel;
	private EditText et_address;
	private RadioGroup rg_sex;
	private RadioButton rb_male;
	private RadioButton rb_female;
	private String sex;
	private RadioButton checkRadioButton;
	private PopupWindow pop = null;
	private Button copy;
	private LinearLayout parent;
	private View parentView;
	private String PasswordType;
	private List<Object> list;
    private String UserID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parentView = getLayoutInflater().inflate(
				R.layout.activity_personalinformationmanager, null);
		setContentView(parentView);
		initNavBar("个人信息管理", "<返回", "保存");
		setAction(null);// user this------
		IsLocal = false;// user this------
		fetchUIFromLayout();
		setListener();
		init();
        dataLoading();
	}

	private void dataLoading() {
		showProgressDialog("数据加载中，请稍候...");
		String url = NetworkSetInfo.getServiceUrl()
				+ "/UserManagerService/queryUser";
		RequestParams params = new RequestParams();
		String condition[] = { "UserID"};
		String value[] = {UserID};
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				dismissProgressDialog();
				new AlertDialog.Builder(PersonalInformationManagerActivity.this)
						.setTitle("错误")
						.setMessage("网络访问失败，请检查网络！" + error.toString())
						.setPositiveButton("确定", null).show();
				return;
			}

			@Override
			public void onSuccess(String content) {
				dismissProgressDialog();
				dataResult = dataDecode
						.decode(content,
								"com.beautyhealth.UserCenter.Entity.UserMessage");
				if (dataResult != null) {
					// user this------
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						UserMessage msg = (UserMessage) realData.getResult().get(0);
						ISqlHelper iSqlHelper = new SqliteHelper(null,
								getApplicationContext());
						String sqlStr = "update UserMessage set UserAddress ='"
								+ msg.UserAddress + "',UserSex='" + msg.UserSex
								+ "',UserTel='" + msg.UserTel
								+ "',UserBirthday='" + msg.UserBirthday
								+ "',UserRealName='" + msg.UserRealName
								+ "' where UserID = '"+tv_phone.getText().toString()+"'";
						iSqlHelper.SQLExec(sqlStr);
						handler.sendEmptyMessage(0);
					}else {
						new AlertDialog.Builder(
								PersonalInformationManagerActivity.this)
								.setTitle("错误")
								.setMessage("网络数据加载失败")
								.setPositiveButton("确定", null).show();
						return;
					}
				}
			}

		});
	}


	private void init() {
		// 从数据库中将信息查出来
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {
			UserMessage userMessage = (UserMessage) list.get(0);
            UserID = userMessage.UserID;
			tv_phone.setText(userMessage.UserID);
			et_registercode.setText(userMessage.UUID);
			et_name.setText(userMessage.UserRealName);
			et_tel.setText(userMessage.UserTel);
			et_address.setText(userMessage.UserAddress);
			et_birthday.setText(userMessage.UserBirthday);
			PasswordType = userMessage.PasswordType;
			if (userMessage.UserSex.equals("男")) {
				rb_male.setChecked(true);
			} else {
				rb_female.setChecked(true);
			}
		}
	}

	private void setListener() {
		btn_getregiatercode.setOnClickListener(this);
		btn_updatepassword.setOnClickListener(this);
	    rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {               
	            @Override  
	           public void onCheckedChanged(RadioGroup group, int checkedId) {  
	                   
	                 //点击事件获取的选择对象  
	                 checkRadioButton = (RadioButton) rg_sex.findViewById(checkedId);  
					 sex= checkRadioButton.getText().toString();             
//					 Toast.makeText(getApplicationContext(), checkRadioButton.getText().toString(), Toast.LENGTH_SHORT).show();
	            }
	         });
	    et_birthday.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Calendar c = Calendar.getInstance();
				new DatePickerDialog(PersonalInformationManagerActivity.this,
						new DatePickerDialog.OnDateSetListener() {				
					@Override
					public void onDateSet(DatePicker view, int year, int monthOfYear,
							int dayOfMonth) {
						int month = (monthOfYear + 1);
						String strdate = year+"-";
						if(month<10){
							strdate = strdate+"0"+month+"-";
						}else{
							strdate = strdate+month+"-";
						}
						if(dayOfMonth<10){
							strdate =strdate+"0"+dayOfMonth+" ";
						}else{
							strdate = strdate+dayOfMonth+" ";
						}
					
						et_birthday.setText(strdate);
					}
				},c.get(Calendar.YEAR)
				 ,c.get(Calendar.MONTH)
				 ,c.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
	    et_registercode.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				
				et_registercode.setBackgroundColor(0xaaaaa);
				
				pop.showAsDropDown(et_registercode, 0, 0);
				return false;
			}
		});
	  //点击窗体
		parent.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				pop.dismiss();
				//et_registercode.setSelectAllOnFocus(false);
				//et_registercode.setTextIsSelectable(false);
			}
		});
		copy.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				copy(et_registercode.getText().toString(), getApplicationContext());
				pop.dismiss();
			}
		});
	}

	@SuppressLint("InflateParams")
	@SuppressWarnings("deprecation")
	private void fetchUIFromLayout() {
		tv_phone = (TextView) findViewById(R.id.tv_phone);
		et_registercode = (EditText) findViewById(R.id.et_registercode);
		et_name = (EditText) findViewById(R.id.et_name);
		et_tel = (EditText) findViewById(R.id.et_tel);
		et_address = (EditText) findViewById(R.id.et_address);
		et_birthday = (EditText) findViewById(R.id.et_birthday);
		et_birthday.setInputType(InputType.TYPE_NULL);
		rg_sex = (RadioGroup) findViewById(R.id.rg_sex);
		rb_male = (RadioButton) findViewById(R.id.rb_male);
		rb_female = (RadioButton) findViewById(R.id.rb_female);
		btn_getregiatercode = (Button) findViewById(R.id.btn_getregiatercode);
		btn_updatepassword = (Button) findViewById(R.id.btn_updatepassword);
		

		View view = getLayoutInflater().inflate(
				R.layout.item_copy_popupwindows, null);
		pop = new PopupWindow(view,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		parent = (LinearLayout) view.findViewById(R.id.parent);
		copy = (Button) view.findViewById(R.id.copy);
		// 背景设置 无阴影
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.setFocusable(true);// 获取焦点，设置当前窗体为操作窗体
		pop.setOutsideTouchable(true); // 外围点击dismiss
		pop.setContentView(view);
	}

	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		// 重新生成注册码
		case R.id.btn_getregiatercode:
			getNewUUID();
			break;
		// 密码修改
		case R.id.btn_updatepassword:
			intent.setClass(getApplicationContext(),
					UpdatePasswordActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void getNewUUID() {
		String url = NetworkSetInfo.getServiceUrl()
				+ "/UserManagerService/createRegisterNumber";
		RequestParams params = new RequestParams();
		String condition[] = { "UserID" };
		String value[] = { tv_phone.getText().toString() };
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				new AlertDialog.Builder(PersonalInformationManagerActivity.this)
						.setTitle("错误")
						.setMessage("网络访问失败，请检查网络！" + error.toString())
						.setPositiveButton("确定", null).show();
				return;
			}

			@Override
			public void onSuccess(String content) {
				dataResult = dataDecode
						.decode(content,
								"com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage");
				if (dataResult != null) {
					// user this------
					DataResult realData = (DataResult) dataResult;
					if (realData.getResultcode().equals("1")) {
						ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData
								.getResult().get(0);
						if (msg.getResult().equals("1")) {
							et_registercode.setText(msg.tip);
							ISqlHelper iSqlHelper = new SqliteHelper(null,
									getApplicationContext());
							String sqlStr = "update UserMessage set UUID ='"
									+ msg.tip + "'where Indexmy = '1'";
							iSqlHelper.SQLExec(sqlStr);
							new AlertDialog.Builder(
									PersonalInformationManagerActivity.this)
									.setTitle("成功")
									.setMessage("新注册码为:" + msg.tip)
									.setPositiveButton(
											"确定",
											new DialogInterface.OnClickListener() {
												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													return;
												}
											}).setCancelable(false).show();
						} else {
							new AlertDialog.Builder(
									PersonalInformationManagerActivity.this)
									.setTitle("错误")
									.setMessage("生成失败，请重新生成注册码")
									.setPositiveButton("确定", null).show();
							return;
						}
					}
				}
			}

		});
	}

	/**
	 * 右按钮监听函数,保存
	 */
	public void onNavBarRightButtonClick(View view) {
		// 将数据保存到本地
		if (et_name.getText().toString().equals("")
				|| et_tel.getText().toString().equals("")
				|| et_address.getText().toString().equals("")) {
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

		} else {
			ISqlHelper iSqlHelper = new SqliteHelper(null,
					getApplicationContext());
			String sqlStr = "update UserMessage set UserAddress ='"
					+ et_address.getText().toString() + "',UserSex='" + sex
					+ "',UserTel='" + et_tel.getText().toString()
					+ "',UserBirthday='" + et_birthday.getText().toString()
					+ "',UserRealName='" + et_name.getText().toString()
					+ "' where UserID = '"+tv_phone.getText().toString()+"'";
			iSqlHelper.SQLExec(sqlStr);
			userMsgUpdate();
		}

	}

	private void userMsgUpdate() {
		showProgressDialog("数据上传中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("UserManagerService", "updateUserInfo");
		Map requestCondition = new HashMap();
		String condition[] = { "UserID", "UserRealName", "UserBirthday",
				"UserTel", "UserSex", "UserAddress" };
		String value[] = { tv_phone.getText().toString(),
				et_name.getText().toString(), et_birthday.getText().toString(),
				tv_phone.getText().toString(), sex,
				et_address.getText().toString() };
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
				ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData
						.getResult().get(0);
				if (msg.getResult().equals("1")) {
					new AlertDialog.Builder(this)
							.setTitle("成功")
							.setMessage(msg.tip)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											return;
										}
									}).setCancelable(false).show();
				} else {
					new AlertDialog.Builder(this)
							.setTitle("失败")
							.setMessage(msg.tip)
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											return;
										}
									}).setCancelable(false).show();
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
			}

		}
	}
	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			init();
		}
	};
	/**
	 * 实现文本复制功能 add by 十三妖
	 * 
	 * @param content
	 */
	public static void copy(String content, Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		cmb.setText(content.trim());
	}

	/**
	 * 实现粘贴功能 add by 十三妖
	 * 
	 * @param context
	 * @return
	 */
	public static String paste(Context context) {
		// 得到剪贴板管理器
		ClipboardManager cmb = (ClipboardManager) context
				.getSystemService(Context.CLIPBOARD_SERVICE);
		return cmb.getText().toString().trim();
	}

	
	

}
