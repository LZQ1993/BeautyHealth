package com.beautyhealth.PrivateDoctors.Activity;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.Infrastructure.CWDataDecode.JsonDecode;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDataRequest.RequestUtility;
import com.beautyhealth.PrivateDoctors.Entity.DoctorBrief;

public class DoctorBrieflyActivity extends DataRequestActivity implements
		OnItemClickListener {
	private ListView lv_doctorbrief;
	private List<DoctorBrief> doctorInfo;
	// private List<String> picUrl = new ArrayList<String>();
	private ListViewAdapter listAdapter;
	private String hospitalName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pd_doctorbriefly);
		setAction(null);// user this------
		IsLocal = false;// user this------
		hospitalName = getIntent().getStringExtra("HospitalName");
		initNavBar(hospitalName, "<返回", null);
		initList();
		fetchUIFromLayout();
		setListener();
		dataUpLoading();

	}

	private void initList() {
		doctorInfo = new ArrayList<DoctorBrief>();
		
	}

	private void fetchUIFromLayout() {
		lv_doctorbrief = (ListView) findViewById(R.id.lv_doctorbrief);
		LinearLayout empty_view = (LinearLayout)findViewById(R.id.empty_view);
		empty_view.setOnClickListener(new OnClickListener() {		
			@Override
			public void onClick(View v) {
				
				dataUpLoading();
			}
		});
		lv_doctorbrief.setEmptyView(empty_view);
	}

	private void setListener() {
		listAdapter = new ListViewAdapter(this);
		lv_doctorbrief.setAdapter(listAdapter);
		lv_doctorbrief.setOnItemClickListener((OnItemClickListener) this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void dataUpLoading() {
		registeBroadcast();
		showProgressDialog("数据加载中，请稍候...");
		// user this------
		ClassFullName = "com.beautyhealth.PrivateDoctors.Entity.DoctorBrief";
		// user this------
		RequestUtility myru = new RequestUtility(this);
		// myru.setIP(NetworkInfo.getServiceUrl());
		myru.setIP(null);
		myru.setMethod("HandyDoctorService", "queryHandyDoctor");
		Map requestCondition = new HashMap();
		String condition[] = { "HospitalID","rows","page" };
		String value[] = {getIntent().getStringExtra("HospitalID"),"18","-1" };
		String strJson = JsonDecode.toJson(condition, value);
		requestCondition.put("json", strJson);
		myru.setParams(requestCondition);
		this.setRequestUtility(myru);
		this.requestData();
	}

	/**
	 * 适配器内部类
	 */
	private class ListViewAdapter extends BaseAdapter {

		private List<View> itemViews;

		public ListViewAdapter(Context context) {
			// 初始化一个布局加载器
			LayoutInflater inflater = LayoutInflater.from(context);
			// 创建一个自相布局数组
			itemViews = new ArrayList<View>();
			for (int n = 0; n < doctorInfo.size(); n++) {
				DoctorBrief docterBerief = doctorInfo.get(n);

				View itemView = inflater.inflate(
						R.layout.activity_pd_doctorbriefly_item, null);
				// 初始化子布局
				TextView tv_doctorName = (TextView) itemView
						.findViewById(R.id.doctorName);
				TextView tv_ClassName = (TextView) itemView
						.findViewById(R.id.ClassName);
				TextView tv_briefContent = (TextView) itemView
						.findViewById(R.id.briefContent);
				ImageView iv_doctorPic = (ImageView) itemView.findViewById(R.id.doctorPhoto);
				tv_doctorName.setText(docterBerief.getDoctorName());
				tv_ClassName.setText(docterBerief.getClassName());
				tv_briefContent.setText("简介:"+docterBerief.getDoctorDescription());
				iv_doctorPic.setImageResource(R.drawable.doctorpic);
				if(docterBerief.DoctorImgUrl.length()>0){
					//String webaddrss = "http://www.xqtian.com/course/Files/Upload/images//06578fac-7d2e-4df9-90cb-59f491773466.jpg";
					String webaddrss = NetworkSetInfo.getServiceUrl()+"/"+docterBerief.DoctorImgUrl.substring(2, docterBerief.DoctorImgUrl.length());	
					loadingpic(itemView, webaddrss);
				}else{
					iv_doctorPic.setImageResource(R.drawable.doctorpic);
				}
				
				itemViews.add(itemView);
			}
		}

		private void loadingpic(View itemView, String picUrl) {
			// 启动线程获取网络图片填充
			LoadImageTask lit = new LoadImageTask(itemView, picUrl);
			lit.execute(picUrl);
		}

		@Override
		public int getCount() {
			return itemViews.size();
		}

		@Override
		public Object getItem(int position) {
			return itemViews.get(position);
		}

		@Override
		public long getItemId(int position) {
			return itemViews.get(position).getId();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return itemViews.get(position);
		}
	}

	@Override
	public void updateView() {
		super.updateView();
		dismissProgressDialog();
		if (dataResult != null) {
			// user this------
			DataResult realData = (DataResult) dataResult;
			if (realData.getResultcode().equals("1")) {
				List<DoctorBrief> info = new ArrayList<DoctorBrief>();
				for (int i = 0; i < realData.getResult().size(); i++) {
					DoctorBrief db = (DoctorBrief) realData.getResult().get(i);
					info.add(db);
				}
				handler.sendMessage(handler.obtainMessage(100, info));
			} else {
				new AlertDialog.Builder(this)
						.setTitle("提示")
						.setMessage("暂无数据！")
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										finish();
									}
								}).setCancelable(false).show();
				return;
			}
		} else {
			new AlertDialog.Builder(this)
					.setTitle("错误")
					.setMessage("网络加载失败！")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									finish();
								}
							}).setCancelable(false).show();
			return;
		}
	}

	Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			doctorInfo.clear();
			doctorInfo.addAll((List<DoctorBrief>) msg.obj);
			listAdapter.notifyDataSetChanged(); // 发送消息通知ListView更新
			listAdapter = new ListViewAdapter(getApplicationContext());
			lv_doctorbrief.setAdapter(listAdapter); // 重新设置ListView的数据适配器

		}
	};

	// 加载图片的异步任务
	class LoadImageTask extends AsyncTask<String, Void, Bitmap> {
		private View resultView;
		private String picUrl;

		LoadImageTask(View resultView, String picUrl) {
			this.resultView = resultView;
			this.picUrl = picUrl;
		}

		// doInBackground完成后才会被调用
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// 调用setTag保存图片以便于自动更新图片
			// resultView.setTag(bitmap);
			if (bitmap == null) {
				((ImageView) resultView.findViewById(R.id.doctorPhoto))
						.setImageResource(R.drawable.doctorpic);
			} else {

				((ImageView) resultView.findViewById(R.id.doctorPhoto))
						.setImageBitmap(bitmap);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {           
             Intent intent = new Intent();
             Bundle bundle = new Bundle();
             bundle.putSerializable("DoctorInfo",doctorInfo.get(position));
             intent.putExtras(bundle);
             intent.putExtra("HospitalName", getIntent().getStringExtra("HospitalName"));
             intent.setClass(getApplicationContext(), DoctorAllInfoAcitivty.class);
             startActivity(intent);
             overridePendingTransition(R.anim.slide_up_in, R.anim.slide_down_out);
		
	}
}
