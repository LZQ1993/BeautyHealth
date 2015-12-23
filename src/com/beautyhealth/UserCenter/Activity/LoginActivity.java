package com.beautyhealth.UserCenter.Activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.beautyhealth.Infrastructure.CWUtility.ToastUtil;
import com.beautyhealth.UserCenter.Assistant.MyCountTimer;
import com.beautyhealth.UserCenter.Entity.UserMessage;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class LoginActivity extends DataRequestActivity implements
		OnClickListener {
	private Button btn_login;
	private Button btn_sendregistercode;
	private Button btn_registercodelogin;
	private EditText et_phone;
	private EditText et_password;
	private String UserName;
	private String Password;
	private String PasswordType;
	private CheckBox cb_jizhumima;
	private List<Object> list;
	private SharedPreferences config;
	private RelativeLayout rl_user;
	private ImageView login_picture;
    private Boolean isTureShow=false;
	Intent intent = new Intent();
	Bundle bndle = new Bundle();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		ISqlHelper iSqlHelper = new SqliteHelper(null, getApplicationContext());
		List<Object> list = iSqlHelper.Query(
				"com.beautyhealth.UserCenter.Entity.UserMessage", null);
		if (list.size() > 0) {
			initNavBar("登陆","<返回", null);
			isTureShow=false;
			
		} else {
			initNavBar("登陆", null, null);
			isTureShow = true;
		}
		setAction(null);// user this------
		IsLocal = false;// user this------
		fetchUIFromLayout();
		setListener();

	}

	@Override
	protected void onResume() {
		super.onResume();
		init();
	}

	private void init() {
		// 记住密码

		config = getSharedPreferences("config", MODE_PRIVATE);
		boolean isChecked = config.getBoolean("isChecked", false);
		if (isChecked) {
			et_phone.setText(config.getString("UserName", ""));
			et_password.setText(config.getString("Password", ""));
		}
		cb_jizhumima.setChecked(isChecked);

	}

	private void setListener() {
		btn_login.setOnClickListener(this);
		btn_sendregistercode.setOnClickListener(this);
		btn_registercodelogin.setOnClickListener(this);
	}

	private void fetchUIFromLayout() {
		btn_login = (Button) findViewById(R.id.btn_login);
		btn_sendregistercode = (Button) findViewById(R.id.btn_sendregistercode);
		btn_registercodelogin = (Button) findViewById(R.id.btn_registercodelogin);
		et_phone = (EditText) findViewById(R.id.et_phone);
		et_password = (EditText) findViewById(R.id.et_password);
		cb_jizhumima = (CheckBox) findViewById(R.id.cb_jizhumima);
		rl_user = (RelativeLayout) findViewById(R.id.rl_user);
		login_picture = (ImageView) findViewById(R.id.login_picture);

		Animation anim = AnimationUtils.loadAnimation(this, R.anim.login_anim);
		anim.setFillAfter(true);
		rl_user.startAnimation(anim);

		Animation anim2 = AnimationUtils.loadAnimation(this,
				R.anim.login_head_anim);
		anim2.setFillAfter(true);
		login_picture.startAnimation(anim2);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {

		// 登陆
		case R.id.btn_login:
			PasswordType = "2";
			loginTask(PasswordType);

			break;

		// 发送验证码
		case R.id.btn_sendregistercode:
			if (et_phone.getText().toString().equals("")) {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("手机号不能为空！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										return;
									}
								}).setCancelable(false).show();
			} else {
				getNetPassword();
			}
			break;
		// 验证码登陆
		case R.id.btn_registercodelogin:

			PasswordType = "1";
			loginTask(PasswordType);
			break;

		default:
			break;
		}
	}

	private void loginTask(String passwordType) {
		registeBroadcast();
		showProgressDialog("正在登录，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("UserManagerService", "loginUserManager");
		Map requestCondition = new HashMap();
		String condition[] = { "UserID", "Password", "PasswordType" };
		String value[] = { et_phone.getText().toString(),
				et_password.getText().toString(), passwordType };

		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);

		this.setRequestUtility(myru);
		this.requestData();

	}

	private void getNetPassword() {
		MyCountTimer timeCount = new MyCountTimer(btn_sendregistercode,
				0xfff30008, 0xff969696);// 传入了文字颜色值
		timeCount.start();
		String url = NetworkSetInfo.getServiceUrl()
				+ "/UserManagerService/returnCode";
		RequestParams params = new RequestParams();
		String condition[] = { "UserID" };
		String value[] = { et_phone.getText().toString() };
		String strJson = JsonDecode.toJson(condition, value);
		params.put("json", strJson);
		HttpUtil.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(Throwable error) {
				new AlertDialog.Builder(LoginActivity.this).setTitle("错误")
						.setMessage("网络访问失败，60s后再重新发送")
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
							Toast.makeText(getApplicationContext(), "发送成功",
									Toast.LENGTH_SHORT).show();
						} else {
							Toast.makeText(getApplicationContext(),
									"发送失败，60s后请重新发送", Toast.LENGTH_SHORT)
									.show();
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
				ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData
						.getResult().get(0);
				if (msg.getResult().equals("1")) {
					ISqlHelper iSqlHelper = new SqliteHelper(null,
							getApplicationContext());
					iSqlHelper
							.SQLExec("delete from UserMessage where Indexmy='1'");// 删除表中原有的数据，保证只有一条
					UserMessage userMessage = new UserMessage();
					userMessage.Indexmy = "1";
					userMessage.UserID = et_phone.getText().toString();
					userMessage.Password = et_password.getText().toString();
					userMessage.UUID = msg.tip;
					userMessage.PasswordType = PasswordType;
					iSqlHelper.Insert(userMessage);

					UserName = et_phone.getText().toString();
					Password = et_password.getText().toString();
					isRemember();
					if (isTureShow) {
						ToastUtil.show(getApplicationContext(),
								"登录成功,默认密码：123456，建议您立即修改");
					} else {
						ToastUtil.show(getApplicationContext(),
								"登录成功");
					}

					// 用户信息页面跳转
					Class<?> gotoClz = null;
					try {
						gotoClz = Class.forName(getIntent().getStringExtra(
								"goto"));
					} catch (Exception e) {
					}
					if (gotoClz != null) {
						Intent intent = new Intent(LoginActivity.this, gotoClz);
						intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						startActivity(intent);
					}
					finish();
					return;
				} else {
					new AlertDialog.Builder(this)
							.setTitle("失败")
							.setMessage("登录失败")
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
						.setTitle("错误")
						.setMessage("网络数据获取失败")
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

	// 是否记住密码
	private void isRemember() {

		Editor edit = config.edit();
		boolean isChecked = cb_jizhumima.isChecked();
		edit.putBoolean("isChecked", isChecked);
		if (isChecked) {
			edit.putString("UserName", UserName)
					.putString("Password", Password);
		} else {
			edit.remove("UserName");
			edit.remove("Password");
		}
		edit.commit();

	}

}
