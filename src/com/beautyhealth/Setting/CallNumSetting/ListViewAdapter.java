package com.beautyhealth.Setting.CallNumSetting;

import java.util.HashMap;
import java.util.List;

import com.beautyhealth.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter {

	public int SelectedPosition=0;
	private Context context;
	List<Boolean> res1;
	
	private List<String[]> params;
	// 用于记录每个RadioButton的状态，并保证只可选一个
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();

	class ViewHolder {
		TextView tvName;
		RadioButton rb_state;
		public TextView getTvName() {
			return tvName;
		}
		public void setTvName(TextView tvName) {
			this.tvName = tvName;
		}
		public RadioButton getRb_state() {
			return rb_state;
		}
		public void setRb_state(RadioButton rb_state) {
			this.rb_state = rb_state;
		}
		
		
	}

	public ListViewAdapter(Context context, List<String[]> _params,List<Boolean> res1) {
		// TODO Auto-generated constructor stub
		this.params = _params;
		this.context = context;
		this.res1=res1;
		
	}

	@Override
	public int getCount() {
		return params.get(0).length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return params.get(0)[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		// 页面
		ViewHolder holder;
		String citynames = params.get(0)[position];
		String phone = params.get(1)[position];
		LayoutInflater inflater = LayoutInflater.from(context);
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_radiolistview, null);
			holder = new ViewHolder();
			// holder.rb_state = (RadioButton) convertView
			// .findViewById(R.id.rb_light);
			holder.tvName = (TextView) convertView
					.findViewById(R.id.tv_device_name);
			holder.rb_state=(RadioButton)convertView.findViewById(R.id.rb_light);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvName.setText(citynames);
		final RadioButton radio=holder.rb_state ;
		holder.rb_state.setChecked(res1.get(position));
		if(res1.get(position)==true){
			SelectedPosition=position;
		}
		
		holder.rb_state.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// 重置，确保最多只有一项被选中
				
				res1.set(position, true);
				for(int i=0;i<res1.size();i++){
					if(i!=position){
						res1.set(i, false);
					}
					
				}
				SelectedPosition=position;
				ListViewAdapter.this.notifyDataSetChanged();
				
				
				
			}
		});
		return convertView;
	}

	private RadioButton findViewById(int rbLight) {
		// TODO Auto-generated method stub
		return null;
	}
}
