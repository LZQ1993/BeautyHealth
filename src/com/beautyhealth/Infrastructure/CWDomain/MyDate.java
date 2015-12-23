package com.beautyhealth.Infrastructure.CWDomain;

import java.util.Calendar;
import java.util.Date;

import com.beautyhealth.Infrastructure.CWUtility.TypeConverHelper;

public class MyDate {
	public int Year=1900;
	public int Month=1;
	public int Day=1;
	public int Hour=0;
	public int Minute=0;
	public int Second=0;
	public int Week;

	public Date getDate() {
		return TypeConverHelper.getSpecialDate(this);
	}
	public void setDate(Calendar calendar) {
		this.Year=calendar.get(Calendar.YEAR);
		this.Month=calendar.get(Calendar.MONTH);
		this.Day=calendar.get(Calendar.DAY_OF_MONTH);
		this.Hour=calendar.get(Calendar.HOUR_OF_DAY);
		this.Minute=calendar.get(Calendar.MINUTE);
		this.Second=calendar.get(Calendar.SECOND);
		this.Week=calendar.get(Calendar.DAY_OF_WEEK);
		
	}
}
