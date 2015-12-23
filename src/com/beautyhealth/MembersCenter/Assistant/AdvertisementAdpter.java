package com.beautyhealth.MembersCenter.Assistant;

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
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWDataRequest.NetworkSetInfo;
import com.beautyhealth.Infrastructure.CWDataRequest.ReadNetPicture;
import com.beautyhealth.MembersCenter.Entity.AdvertisementInfo;
import com.beautyhealth.PrivateDoctors.Assistant.HospitalListAdpter.ViewHolder;
import com.beautyhealth.PrivateDoctors.Entity.Hospital;

public class AdvertisementAdpter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<AdvertisementInfo> advertisementInfo;
	private Context context; 
	private Bitmap bitmap;
	private OnClickListener mycListener; 
	public AdvertisementAdpter(Context context,
			List<AdvertisementInfo> advertisementInfo,
			OnClickListener mycListener) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.advertisementInfo = new ArrayList<AdvertisementInfo>();
		this.mycListener = mycListener;
		this.advertisementInfo.addAll(advertisementInfo);


	}

	public void clearList() {
		this.advertisementInfo.clear();
	}

	public void updateList(List<AdvertisementInfo> advertisementInfo) {
		this.advertisementInfo.addAll(advertisementInfo);

	}

	public static class ViewHolder {
    TextView tv_ad_content,tv_mc_time;
    ImageView iv_ad;
	}

	@Override
	public int getCount() {
		return advertisementInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return advertisementInfo.get(position);
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
					R.layout.activity_memberscenter_item, null);
			holder.tv_ad_content = (TextView) convertView
					.findViewById(R.id.tv_ad_content);
			holder.tv_mc_time= (TextView) convertView
					.findViewById(R.id.tv_mc_time);
			holder.iv_ad = (ImageView) convertView
					.findViewById(R.id.iv_ad);			
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AdvertisementInfo advertisementitem = (AdvertisementInfo) advertisementInfo.get(position);
		holder.tv_ad_content.setText(advertisementitem.AdTitle);
		holder.tv_mc_time.setText(advertisementitem.PublishTime);
		holder.iv_ad.setImageResource(R.drawable.hy_item_pic);
		if(advertisementitem.AdImgUrl.length()>0){
			String webaddrss = NetworkSetInfo.getServiceUrl()+"/"+advertisementitem.AdImgUrl.substring(2, advertisementitem.AdImgUrl.length());
			//String webaddrss = "http://www.xqtian.com/course/Files/Upload/images//06578fac-7d2e-4df9-90cb-59f491773466.jpg";
			LoadPicTask lit = new LoadPicTask(convertView, webaddrss);
			lit.execute(webaddrss);	
		}else{
			holder.iv_ad.setImageResource(R.drawable.hy_item_pic);
		}
		return convertView;
	}
	
	/** 
     * 此方法用来异步加载图片 
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
					((ImageView) resultView.findViewById(R.id.iv_ad))
							.setImageResource(R.drawable.hy_item_pic);
				} else {

					((ImageView) resultView.findViewById(R.id.iv_ad))
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
