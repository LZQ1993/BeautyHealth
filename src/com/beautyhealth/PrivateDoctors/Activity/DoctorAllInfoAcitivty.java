package com.beautyhealth.PrivateDoctors.Activity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.PrivateDoctors.Entity.DoctorBrief;

public class DoctorAllInfoAcitivty extends DataRequestActivity implements
		OnClickListener {
	private DoctorBrief doctorBrief;
	private TextView tv_doctorName, tv_hospitalName, tv_className, tv_mainCute,
			tv_doctorInfo;
	private Button btnAppoint, btnPrice;
	private ImageView iv_db_docotrPic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_doctorbriefly_all);
		setAction(null);// user this------
		IsLocal = true;// user this------
		initNavBar("专家", "<返回", null);
		initData();
		fetchUIFromLayout();
		setListener();

	}

	private void initData() {
		doctorBrief = (DoctorBrief) getIntent().getSerializableExtra(
				"DoctorInfo");
	}

	private void fetchUIFromLayout() {
		tv_doctorName = (TextView) findViewById(R.id.tv_db_doctorName);
		tv_doctorName.setText(doctorBrief.DoctorName);

		tv_hospitalName = (TextView) findViewById(R.id.tv_hospitalName);
		tv_hospitalName.setText(getIntent().getStringExtra("HospitalName"));

		tv_className = (TextView) findViewById(R.id.tv_className);
		tv_className.setText(doctorBrief.ClassName);

		tv_mainCute = (TextView) findViewById(R.id.tv_mainCute);
		tv_mainCute.setText(doctorBrief.MainCute);

		tv_doctorInfo = (TextView) findViewById(R.id.tv_doctorInfo);
		tv_doctorInfo.setText(doctorBrief.DoctorDescription);

		iv_db_docotrPic = (ImageView) findViewById(R.id.iv_db_docotrPic);
		String path = doctorBrief.DoctorImgUrl;
		if (path.length() > 0) {
			String webaddrss = NetworkSetInfo.getServiceUrl() + "/"+ path.substring(2, doctorBrief.DoctorImgUrl.length());
			 //String webaddrss = "http://www.xqtian.com/course/Files/Upload/images//06578fac-7d2e-4df9-90cb-59f491773466.jpg";
			LoadPicTask1 lit = new LoadPicTask1(webaddrss);
			lit.execute(webaddrss);
		}else{
			iv_db_docotrPic.setImageResource(R.drawable.userphoto);	
		}

		btnPrice = (Button) findViewById(R.id.btn_price);
		btnPrice.setText(Html.fromHtml("<u>关于价格</u>"));// 下划线

		btnAppoint = (Button) findViewById(R.id.btn_appoint);

	}

	private void setListener() {
		btnAppoint.setOnClickListener((OnClickListener) this);
	}

	/**
	 * 点击more触发的方法，弹出显示所有年级学科
	 * 
	 * @param view
	 */
	public void showAllSubject(View view) {
		new AlertDialog.Builder(this).setTitle("提示")
				.setMessage("亲~具体价格，callcenter会电话联系您，根据具体病情确定哦~")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						return;
					}
				}).setCancelable(false).show();
	}

	@Override
	public void onClick(View v) {
		if (v == btnAppoint) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putSerializable("DoctorInfo", (DoctorBrief) getIntent()
					.getSerializableExtra("DoctorInfo"));
			intent.putExtras(bundle);
			intent.putExtra("HospitalName",
					getIntent().getStringExtra("HospitalName"));
			intent.setClass(getApplicationContext(), AppointAcitivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
		}

	}

	/**
	 * 此方法用来异步加载图片
	 * 
	 * @param imageview
	 * @param path
	 */
	// 加载图片的异步任务
	class LoadPicTask1 extends AsyncTask<String, Void, Bitmap> {
		private String picUrl;

		LoadPicTask1(String picUrl) {
			this.picUrl = picUrl;
		}

		// doInBackground完成后才会被调用
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// 调用setTag保存图片以便于自动更新图片
			// resultView.setTag(bitmap);
			if (bitmap == null) {
				iv_db_docotrPic.setImageResource(R.drawable.userphoto);
			} else {

				iv_db_docotrPic.setImageBitmap(bitmap);
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
