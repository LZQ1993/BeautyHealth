package com.beautyhealth.PersonHealth.MyLocation.Activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;
import com.beautyhealth.Infrastructure.CWDataRequest.DataRequestActivity;

public class RouteStepShowTest extends DataRequestActivity {
	List<ItemBean> list=new ArrayList<ItemBean>();
	 private TabBarFragment tabBarFragment;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_routesteplist);
		initNavBar("经过道路", "<返回", null);
		if(findViewById(R.id.fragment_container)!=null){
			if (savedInstanceState!=null) {
				return;
			}
			tabBarFragment = new TabBarFragment();
			tabBarFragment.setArguments(getIntent().getExtras());
			getFragmentManager().beginTransaction().add(R.id.fragment_container, tabBarFragment).commit();
		}
		ListView listView=(ListView) findViewById(R.id.listView);
		TextView startpoint=(TextView) findViewById(R.id.startpoint);
		TextView stoppoint=(TextView) findViewById(R.id.stoppoint);
		Intent _intent=getIntent();
		Bundle routeTypeBundle=_intent.getBundleExtra("routeType");
		String routeType= routeTypeBundle.getString("routeType") ;
		Bundle routeStepBundle=_intent.getBundleExtra("routeStep");
		startpoint.clearFocus();
		startpoint.setText(getIntent().getStringExtra("start"));
		
		stoppoint.clearFocus();
		stoppoint.setText(getIntent().getStringExtra("end"));
		ArrayList<RounteStepInfo> driveSteps=(ArrayList<RounteStepInfo>)routeStepBundle.getSerializable("routeStep");
		//将下面的数据再整理后添加到LIST列表中即可
		if(routeType.equals("1")){
			for(int i=0;i<driveSteps.size();i++){
				BusRouteStepInfo aRSI=(BusRouteStepInfo)driveSteps.get(i);
				  list.add(new ItemBean("公交车名:"+aRSI.BustLineName));
			}
		}else if(routeType.equals("2")){
			//
			for(int i=0;i<driveSteps.size();i++){
				RounteStepInfo aRSI=driveSteps.get(i);
				list.add(new ItemBean("道路名称："+aRSI.StepName
						+"\n"+"距离（米）："+aRSI.Distance));
			}
		}else if(routeType.equals("3")){
			
			for(int i=0;i<driveSteps.size();i++){
				RounteStepInfo aRSI=driveSteps.get(i);
				list.add(new ItemBean("道路名称："+aRSI.StepName
						+"\n"+"距离（米）："+aRSI.Distance));
				 
			}
		}
		 listView.setAdapter(new MyAdapter(this, list));
	}
	 
}
