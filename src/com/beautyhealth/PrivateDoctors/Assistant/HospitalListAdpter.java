package com.beautyhealth.PrivateDoctors.Assistant;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.PrivateDoctors.Entity.Hospital;

public class HospitalListAdpter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<Hospital> hospital;
	private Context context;
	private Bitmap bitmap;
	private OnClickListener mycListener;

	public HospitalListAdpter(Context context, List<Hospital> hospital,
			OnClickListener mycListener) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.hospital = new ArrayList<Hospital>();
		this.mycListener = mycListener;
		this.hospital.addAll(hospital);

	}

	public void clearList() {
		this.hospital.clear();
	}

	public void updateList(List<Hospital> hospital) {
		this.hospital.addAll(hospital);

	}

	public static class ViewHolder {
		TextView tv_hospitalName, tv_hospitalLevel, tv_hospitalBriefly;
		ImageView iv_hospitalPhoto;
		Button btn_seeall;
	}

	@Override
	public int getCount() {
		return hospital.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return hospital.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			// 可以理解为从vlist获取view 之后把view返回给ListView
			convertView = inflater.inflate(
					R.layout.activity_searchhospital_item, null);
			holder.tv_hospitalName = (TextView) convertView
					.findViewById(R.id.hospitalName);
			holder.tv_hospitalLevel = (TextView) convertView
					.findViewById(R.id.hospitalLevel);
			holder.tv_hospitalBriefly = (TextView) convertView
					.findViewById(R.id.hospitalBriefly);
			holder.iv_hospitalPhoto = (ImageView) convertView
					.findViewById(R.id.hospitalPhoto);
			holder.btn_seeall = (Button) convertView
					.findViewById(R.id.btn_see_all);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		Hospital hospitalitem = (Hospital) hospital.get(position);
		holder.tv_hospitalName.setText(hospitalitem.HospitalName);
		holder.tv_hospitalLevel.setText(hospitalitem.HospitalLevel);
		holder.tv_hospitalBriefly.setText(hospitalitem.HospitalBriefly);

		holder.iv_hospitalPhoto.setImageResource(R.drawable.doctorpic);
		String webaddrss = NetworkSetInfo.getServiceUrl()
				+ "/"
				+ hospitalitem.HospitalImgUrl.substring(2,
						hospitalitem.HospitalImgUrl.length());
		// String webaddrss =
		// "http://www.xqtian.com/course/Files/Upload/images//06578fac-7d2e-4df9-90cb-59f491773466.jpg";
		LoadPicTask lit = new LoadPicTask(convertView, webaddrss);
		lit.execute(webaddrss);

		holder.btn_seeall.setTag(position);
		holder.btn_seeall.setOnClickListener(mycListener);
		return convertView;
	}

	/**
	 * 此方法用来异步加载图片
	 * 
	 * @param imageview
	 * @param path
	 */
	// 加载图片的异步任务
	class LoadPicTask extends AsyncTask<String, Void, Bitmap> {
		private View resultView;
		private String picUrl;

		LoadPicTask(View resultView, String picUrl) {
			this.resultView = resultView;
			this.picUrl = picUrl;
		}

		// doInBackground完成后才会被调用
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			// 调用setTag保存图片以便于自动更新图片
			// resultView.setTag(bitmap);
			if (bitmap == null) {
				((ImageView) resultView.findViewById(R.id.hospitalPhoto))
						.setImageResource(R.drawable.doctorpic);
			} else {

				((ImageView) resultView.findViewById(R.id.hospitalPhoto))
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
}
