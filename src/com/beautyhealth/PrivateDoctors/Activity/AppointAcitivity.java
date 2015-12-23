package com.beautyhealth.PrivateDoctors.Activity;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;
import com.beautyhealth.PersonHealth.Pedometer.PedometerActivity;
import com.beautyhealth.PersonHealth.Pedometer.StepDetector;
import com.beautyhealth.PrivateDoctors.Activity.DoctorAllInfoAcitivty.LoadPicTask1;
import com.beautyhealth.PrivateDoctors.Entity.DoctorBrief;
import com.beautyhealth.PrivateDoctors.Util.Bimp;
import com.beautyhealth.UserCenter.Entity.UserMessage;

public class AppointAcitivity extends DataRequestActivity implements
		OnClickListener {
	private TextView tv_ap_doctorName, tv_ap_hospitalName, tv_ap_className;
	private EditText et_ap_address, et_ap_user, et_ap_telNum, et_ap_briefly;
	private Button btn_ap_appoint;
	private DoctorBrief doctorBrief;
	private RadioGroup rg_ap_time;
	private String AppointTime;
	private ImageView iv_ap_doctorPic;
	private String UserID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_appoint);
		setAction(null);// user this------
		IsLocal = false;// user this------
		initNavBar("请填写个人信息", "<返回", null);
		initData();
		fetchUIFromLayout();
		setListener();

	}

	private void initData() {
		doctorBrief = (DoctorBrief) getIntent().getSerializableExtra(
				"DoctorInfo");

	}

	private void fetchUIFromLayout() {

		tv_ap_doctorName = (TextView) findViewById(R.id.tv_ap_doctorName);
		tv_ap_doctorName.setText(doctorBrief.DoctorName);
		tv_ap_hospitalName = (TextView) findViewById(R.id.tv_ap_hospitalName);
		tv_ap_hospitalName.setText(getIntent().getStringExtra("HospitalName"));
		tv_ap_className = (TextView) findViewById(R.id.tv_ap_className);
		tv_ap_className.setText(doctorBrief.ClassName);

		et_ap_address = (EditText) findViewById(R.id.et_ap_address);
		et_ap_user = (EditText) findViewById(R.id.et_ap_user);
		et_ap_telNum = (EditText) findViewById(R.id.et_ap_telNum);
		et_ap_briefly = (EditText) findViewById(R.id.et_ap_briefly);

		iv_ap_doctorPic = (ImageView) findViewById(R.id.iv_ap_doctorPic);
		String path = doctorBrief.DoctorImgUrl;
		if (path.length() > 0) {
			String webaddrss = NetworkSetInfo.getServiceUrl() + "/"
					+ path.substring(2, doctorBrief.DoctorImgUrl.length());
			// String webaddrss =
			// "http://www.xqtian.com/course/Files/Upload/images//06578fac-7d2e-4df9-90cb-59f491773466.jpg";
			LoadPicTask2 lit = new LoadPicTask2(webaddrss);
			lit.execute(webaddrss);
		} else {
			iv_ap_doctorPic.setImageResource(R.drawable.userphoto);
		}

		rg_ap_time = (RadioGroup) findViewById(R.id.rg_ap_time);
		AppointTime = "2";
		btn_ap_appoint = (Button) findViewById(R.id.btn_ap_appoint);
	}

	private void setListener() {
		btn_ap_appoint.setOnClickListener((OnClickListener) this);
		rg_ap_time.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if (checkedId == R.id.rb_ap_time1) {
					AppointTime = "1";
				}
				if (checkedId == R.id.rb_ap_time2) {
					AppointTime = "2";
				}
				if (checkedId == R.id.rb_ap_time3) {
					AppointTime = "3";
				}
			}

		});
	}

	@Override
	public void onClick(View v) {
		if (v == btn_ap_appoint) {
			if (AppointTime == null
					|| (et_ap_address.getText().toString().equals("") || et_ap_address
							.getText().toString() == null)
					|| (et_ap_user.getText().toString().equals("") || et_ap_user
							.getText().toString() == null)
					|| (et_ap_telNum.getText().toString().equals("") || et_ap_telNum
							.getText().toString() == null)
					|| (et_ap_briefly.getText().toString().equals("") || et_ap_briefly
							.getText().toString() == null)) {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("亲~请您填写全部信息，以便于联系您")
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
				List<Object> list = iSqlHelper.Query(
						"com.beautyhealth.UserCenter.Entity.UserMessage", null);
				if (list.size() > 0) {
					UserMessage userMessage = (UserMessage) list.get(0);
					UserID = userMessage.UserID;
					submitData();
				} else {
					new AlertDialog.Builder(this).setTitle("提示")
							.setMessage("您处于离线状态,不能提交数据，请登录再试")
							.setPositiveButton("确定", null).setCancelable(false)
							.show();
				}
			}
		}
	}

	private void submitData() {
		registeBroadcast();
		showProgressDialog("数据上传中，请稍候...");
		ClassFullName = "com.beautyhealth.Infrastructure.CWDomain.ReturnTrasactionMessage";
		RequestUtility myru = new RequestUtility(this);
		myru.setIP(null);
		myru.setMethod("HandyDoctorService", "appointDoctor");
		Map requestCondition = new HashMap();
		SimpleDateFormat sDateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String SubmitTime = sDateFormat.format(new java.util.Date());
		String condition[] = { "UserID", "DoctorID", "LinkUserName", "Address",
				"TelNum", "Briefly", "AppointTime", "SubmitTime" };

		String value[] = { UserID, doctorBrief.DoctorID,
				et_ap_user.getText().toString(),
				et_ap_address.getText().toString(),
				et_ap_telNum.getText().toString(),
				et_ap_briefly.getText().toString(), AppointTime, SubmitTime };
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
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
				ReturnTrasactionMessage msg = (ReturnTrasactionMessage) realData
						.getResult().get(0);
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
											return;
										}
									}).setCancelable(false).show();
				} else {
					new AlertDialog.Builder(this)
							.setTitle("提示")
							.setPositiveButton("确定",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent();
											intent.setClass(
													AppointAcitivity.this,
													MyAppointInfoActivity.class);
											startActivity(intent);
											overridePendingTransition(
													R.anim.slide_up_in,
													R.anim.slide_down_out);
											return;
										}
									}).setMessage(msg.getTip())
							.setCancelable(false).show();
				}
			} else {
				new AlertDialog.Builder(this)
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
		} else {
			new AlertDialog.Builder(AppointAcitivity.this)
					.setTitle("错误")
					.setMessage("数据上传失败,点击确定重新上传")
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

	/**
	 * 此方法用来异步加载图片
	 * 
	 * @param imageview
	 * @param path
	 */
	// 加载图片的异步任务
	class LoadPicTask2 extends AsyncTask<String, Void, Bitmap> {
		private String picUrl;

		LoadPicTask2(String picUrl) {
			this.picUrl = picUrl;
		}

		// doInBackground完成后才会被调用
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// 调用setTag保存图片以便于自动更新图片
			// resultView.setTag(bitmap);
			if (bitmap == null) {
				iv_ap_doctorPic.setImageResource(R.drawable.userphoto);
			} else {

				iv_ap_doctorPic.setImageBitmap(bitmap);
			}
		}

		// 从网上下载图片
		@Override
		protected Bitmap doInBackground(String... params) {
			Bitmap image = null;
			try {
				// new URL对象 把网址传入
				URL url = new URL(params[0]);
				// 取得链接
				URLConnection conn = url.openConnection();
				conn.connect();
				// 取得返回的InputStream
				InputStream is = conn.getInputStream();
				// 将InputStream变为Bitmap
				image = BitmapFactory.decodeStream(is);
				is.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return image;
		}
	}
}
