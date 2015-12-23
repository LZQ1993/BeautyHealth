package com.beautyhealth.Infrastructure.CWUtility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.beautyhealth.Infrastructure.CWDomain.MyDate;

public final class TypeConverHelper {
	
	//字符串转化为时间
	public static Date dateFromString(String sDate) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			Date begin = dfs.parse(sDate);
			return begin;
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	// 时间转化字符串
	public static String stringFromDate(Date sDate) {
		SimpleDateFormat dfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return dfs.format(sDate);
	}

	//指定时间与当前时间 差 
   public static String timeSpanFromCurrent(Date sDate,String returnType){
       Date curDate=new Date(System.currentTimeMillis());//获取当前时间      
	   long l=curDate.getTime()-sDate.getTime();//毫秒
	   if(returnType=="day"){
		   return String.valueOf(l/(24*60*60*1000));
	   }
	   else if(returnType=="hour"){
		   return String.valueOf(l/(60*60*1000));
	   }
	   else if(returnType=="minute"){
		   return String.valueOf(l/(60*1000));
	   }
	   else if(returnType=="second"){
		   return String.valueOf(l/1000);
	   }
	   else if(returnType=="Msecond"){
		   return String.valueOf(l);
	   }
	   else{
		   long day=l/(24*60*60*1000);
		   long hour=(l/(60*60*1000)-day*24);
		   long min=((l/(60*1000))-day*24*60-hour*60);
		   long s=(l/1000-day*24*60*60-hour*60*60-min*60);
		   return day+"天"+hour+"小时"+min+"分"+s+"秒";
	   }
  
   }
   
   public static Date getSpecialDate(MyDate specialDate){
	   String _specialTime=specialDate.Year+"-"+specialDate.Month+"-"+specialDate.Day+" "+specialDate.Hour+":"+specialDate.Minute+":"+specialDate.Second;
	   return dateFromString(_specialTime);
   }
   
   public static String getDateAndWeek(){ 
	   String mYear,mMonth,mDay,mWay,mHour,mMinutes,mSecond;
       Calendar c = Calendar.getInstance(); 
       c.setTimeZone(TimeZone.getTimeZone("GMT+8:00")); 
       mYear = String.valueOf(c.get(Calendar.YEAR));// 获取当前年份 
       mMonth = String.valueOf(c.get(Calendar.MONTH) +1);// 获取当前月份 
       mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码 
       mHour = String.valueOf(c.get(Calendar.HOUR_OF_DAY));
       mMinutes = String.valueOf(c.get(Calendar.MINUTE));
       mSecond = String.valueOf(c.get(Calendar.SECOND));
       mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK)); 
       if("1".equals(mWay)){ 
           mWay ="天"; 
       }else if("2".equals(mWay)){ 
           mWay ="一"; 
       }else if("3".equals(mWay)){ 
           mWay ="二"; 
       }else if("4".equals(mWay)){ 
           mWay ="三"; 
       }else if("5".equals(mWay)){ 
           mWay ="四"; 
       }else if("6".equals(mWay)){ 
           mWay ="五"; 
       }else if("7".equals(mWay)){ 
           mWay ="六"; 
       } 
      // return mYear +"年"+ mMonth +"月"+ mDay+"日"+"\t"+mHour+":"+mMinutes+":"+mSecond+"\t周"+mWay; 
       return mYear +"年"+ mMonth +"月"+ mDay+"日"+"\t周"+mWay; 
   } 
}
