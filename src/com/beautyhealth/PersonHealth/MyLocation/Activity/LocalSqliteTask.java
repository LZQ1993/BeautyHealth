package com.beautyhealth.PersonHealth.MyLocation.Activity;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.Context;

import com.beautyhealth.Infrastructure.CWSqlite.ISqlHelper;
import com.beautyhealth.Infrastructure.CWSqlite.SqliteHelper;

public class LocalSqliteTask implements ICWTask {

	@Override
	public boolean Execute(Context context) {
		SimpleDateFormat formatter = new SimpleDateFormat ("yyyy年MM月dd日 HH:mm:ss ");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        ISqlHelper mysqlhelper=new SqliteHelper(null,context);
        BackSave myUser=new BackSave();
		myUser.Time=str;	
		mysqlhelper.CreateTable("com.beautyhealth.PersonHealth.MyLocation.Activity.BackSave");
		mysqlhelper.Insert(myUser);
		return false;
	}

}
