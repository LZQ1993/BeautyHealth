package com.beautyhealth.PersonHealth.AbilityFunction.Activity;

import java.util.ArrayList;
import java.util.List;

import com.beautyhealth.R;
import com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm;
import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

public class AlarmBaseAdapter extends BaseAdapter {

	private Context cxt;
	private String classFullName="com.beautyhealth.Infrastructure.CWMobileDevice.MyAlarm";
	public ArrayList<MyAlarm> Values=new ArrayList<MyAlarm>();
	ISqlHelper mysql=null;
	
	public AlarmBaseAdapter(Context _cxt){
		cxt=_cxt;		
	    mysql=new SqliteHelper(null, cxt);
		mysql.CreateTable(classFullName);
		List<Object> dataTemp=mysql.Query(classFullName,null);
		for(Object aut:dataTemp){
			Values.add((MyAlarm)aut);
		}
	}
	
	@Override
	public int getCount()
	{
		return Values.size();
	}
	
	@Override
	public Object getItem(int position)
	{
		return null;
	}
	// ��д�÷������÷����ķ���ֵ����Ϊ�б����ID
	@Override
	public long getItemId(int position)
	{
		return position;
	}
	// ��д�÷������÷������ص�View����Ϊ�б��
	@Override
	public View getView(int position, View convertView , ViewGroup parent)
	{
		View resultView=LayoutInflater.from(cxt).inflate(R.layout.custom_alarm_time, null);
		final TextView text=(TextView)resultView.findViewById(R.id.alertTitle);
	  
	    final MyAlarm aAlarm=Values.get(position);

			text.setText(aAlarm.title+"    已提醒："+aAlarm.sampleCount+"次");
			text.setTextSize(20);
			text.setTextColor(Color.BLACK);
			
			 View indicator = resultView.findViewById(R.id.indicator);
	         // Set the initial resource for the bar image.
	         final ImageView barOnOff = (ImageView) indicator.findViewById(R.id.bar_onoff);

	         boolean alarm_enabled=true;
	         if(aAlarm.enabled.equals("0")){
	        	 alarm_enabled=false;
	        	 text.setTextColor(Color.GRAY);
	         }
	         
	         barOnOff.setImageResource(alarm_enabled ?R.drawable.button_open : R.drawable.button_close);

	         // Set the initial state of the clock "checkbox"
	         final CheckBox clockOnOff = (CheckBox) indicator.findViewById(R.id.clock_onoff);
	         clockOnOff.setChecked(alarm_enabled);
	         // Clicking outside the "checkbox" should also change the state.
	         //��checkbox���ü�����ʹ����һ��
	         indicator.setOnClickListener(new OnClickListener() {
	                 public void onClick(View v) {
	                     boolean alarm_enabled=false;
	                     if(clockOnOff.isChecked()){
	                    	 aAlarm.cancelRepeatAlarm(cxt.getApplicationContext(),AlarmActivity.class);
	                    	 clockOnOff.setChecked(false);
	                    	 text.setTextColor(Color.GRAY);
	                    	 text.setText(aAlarm.title+"    已提醒："+0+"次");
	                    	 aAlarm.enabled="0";
	                    	 aAlarm.sampleCount="0";////
	                     }
	                     else{
	                    	//�������� ���е�״̬
	                    	 clockOnOff.setChecked(true);
	                    	 text.setTextColor(Color.BLACK);
	                    	 alarm_enabled=true;
	                    	 aAlarm.enabled="1";
	                    	 aAlarm.sampleCount="0";
	     		    		 aAlarm.setRepeatAlarm(cxt.getApplicationContext(), AlarmActivity.class);
	                     }
	        	         barOnOff.setImageResource(alarm_enabled ?R.drawable.button_open : R.drawable.button_close);

	                     mysql.Update(aAlarm);
	                 }
	         });
		return resultView;
	}
}
