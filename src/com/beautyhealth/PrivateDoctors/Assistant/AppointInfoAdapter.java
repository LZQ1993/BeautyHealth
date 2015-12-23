package com.beautyhealth.PrivateDoctors.Assistant;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Switch;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.PrivateDoctors.Entity.AppointInfo;

public class AppointInfoAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private List<AppointInfo> appointInfo;
	private Context context;


	public AppointInfoAdapter(Context context, List<AppointInfo> appointInfo) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.appointInfo = new ArrayList<AppointInfo>();
		this.appointInfo.addAll(appointInfo);

	}

	public void clearList() {
		this.appointInfo.clear();
	}

	public void updateList(List<AppointInfo> appointInfo) {
		this.appointInfo.addAll(appointInfo);

	}

	public static class ViewHolder {
		TextView tv_hospitalName,tv_class, tv_doctorName, tv_submitTime,tv_appointTime;
	}

	@Override
	public int getCount() {
		return appointInfo.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return appointInfo.get(position);
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
					R.layout.activity_appoint_info_item, null);
			holder.tv_hospitalName = (TextView) convertView
					.findViewById(R.id.ap_info_hospitalName);
			holder.tv_doctorName = (TextView) convertView
					.findViewById(R.id.ap_info_doctorName);
			holder.tv_class = (TextView) convertView
					.findViewById(R.id.ap_info_doctorClass);
			holder.tv_submitTime = (TextView) convertView
					.findViewById(R.id.ap_info_submittime);
			holder.tv_appointTime = (TextView) convertView
					.findViewById(R.id.ap_info_appointtime);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		AppointInfo appointInfoitem = (AppointInfo) appointInfo.get(position);
		holder.tv_hospitalName.setText(appointInfoitem.HospitalName);
		holder.tv_doctorName.setText(appointInfoitem.DoctorName);
		holder.tv_class.setText(appointInfoitem.DoctorClass);
		holder.tv_submitTime.setText(appointInfoitem.SubmitTime);
		if(appointInfoitem.AppointTime.equals("1")){
			holder.tv_appointTime.setText("越快越好");	
		}else if(appointInfoitem.AppointTime.equals("2")){
			holder.tv_appointTime.setText("一周内");	
		}else {
			holder.tv_appointTime.setText("一月内");	
		}

		return convertView;
	}

}
