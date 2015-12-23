package com.beautyhealth.PersonHealth.AbilityFunction.Activity;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWComponent.NavAndTabBarActivity;
import com.beautyhealth.Infrastructure.CWComponent.TabBarFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DeskClockMainActivity extends NavAndTabBarActivity implements OnItemClickListener{

    private ListView mAlarmsList;
	private final String tag_intent="HAVE_ALERMID";
	private final String tag_alarmobj="ALERMOBJ";
	private AlarmBaseAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ling_alarm_clock);
        initNavBar("活动能力", "<返回", "添加");
        FetchUI();        
    }
    
    
    public void onNavBarRightButtonClick(View view) {
    	addNewAlarm();
    }    
   
    @Override
	protected void onResume() {
		//   Auto-generated method stub
		super.onResume();
		loadDataInList();
	}

    private void FetchUI(){
    	mAlarmsList =(ListView) findViewById(R.id.alarms_list);
    	mAlarmsList.setOnItemClickListener(this);
    	
    }
    
	private void loadDataInList(){    	
		hander.sendEmptyMessage(0);////////////////
        adapter=new AlarmBaseAdapter(getApplicationContext());
        mAlarmsList.setAdapter(adapter);  
                        	
    }
	
	private Handler hander = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what){
            case 0:
//            	adapter.clear(); 
            	adapter.notifyDataSetChanged(); //发送消息通知ListView更新
            	 adapter=new AlarmBaseAdapter(getApplicationContext());
            	mAlarmsList.setAdapter(adapter); // 重新设置ListView的数据适配器           	
                break;
            default:
                //do something
                break;
            }
        }
    };
    
    private void addNewAlarm() {
    	Intent _intent=new Intent();
    	_intent.setClass(this, SetAlarmActivity.class);
    	Bundle bundle = new Bundle();
    	bundle.putSerializable(tag_alarmobj, null);
    	bundle.putBoolean(tag_intent, false);
    	_intent.putExtras(bundle);
        startActivity(_intent);
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {      
		Intent _intent=new Intent();
    	_intent.setClass(this, SetAlarmActivity.class);
    	Bundle bundle = new Bundle();
    	bundle.putSerializable(tag_alarmobj, adapter.Values.get(position));
    	bundle.putBoolean(tag_intent, true);
    	_intent.putExtras(bundle);
        startActivity(_intent);
	}

}
