package com.beautyhealth.Infrastructure.CWDataDecode;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.beautyhealth.UserCenter.Activity.LoginActivity;

public class JsonDecode {

	public Object fromJson(String json, String obj, String className)
			throws IllegalAccessException, IllegalArgumentException,
			InstantiationException {

		Class<?> c = null;
		Class<?> cls = null;
		Object dataResultCopy = null;
		Object dataResultCopySon = null;
		try {
			c = Class.forName(obj);// �õ������
			dataResultCopy = c.newInstance();
			cls = Class.forName(className);// �õ����������
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		// �õ����ǵ�����
		Field[] objectFs = c.getFields();
		Field[] sonObjectFs = cls.getFields();

		try {
			JSONObject jsonObject = new JSONObject(json);

			for (int i = 0; i < objectFs.length; i++) {
				Field f = objectFs[i];
				String fieldName = f.getName();
				@SuppressWarnings("unused")
				Class<?> fieldType = f.getType();
				Object value = null;
				if (fieldName.equals("result")) {
					JSONArray jsonArray = jsonObject.getJSONArray(fieldName);
					List<Object> lists = new ArrayList<Object>();
					for (int j = 0; j < jsonArray.length(); j++) {
						JSONObject jsonObjectSon = (JSONObject) jsonArray
								.opt(j);
						dataResultCopySon = cls.newInstance();
						for (int k = 0; k < sonObjectFs.length; k++) {
							Field fson = sonObjectFs[k];
							String fieldSonName = fson.getName();
							Class<?> fieldSonType = fson.getType();
							String str1 = new String(fieldSonType.getName());
							String a[] = str1.split("\\.");
							if (a[2].equals("String")) {
								value = jsonObjectSon.optString(fieldSonName);
							} else if (a[2].equals("int")) {
								value = jsonObjectSon.optInt(fieldSonName);
							} else {
								String str2 = jsonObjectSon.optString(fieldSonName);
								if ((str2 != null || str2 != "")
										&& str2.length() > 4) {
									String str3 = str2.substring(2,
											str2.length() - 2);
									String str4[] = str3.split("\",\"");
									value = str4;
								} else {
									value = new String[] { "" };
								}

							}
							if (value == null || value == "") {
								value = "FF";
							}
							fson.set(dataResultCopySon, value);
						}
						lists.add(dataResultCopySon);
					}
					f.set(dataResultCopy, lists);
				} else {
					try {
						value = jsonObject.optString(fieldName);
					} catch (Exception ex) {
						value = null;
					}
					if (value == null || value == "") {
						value = "FF";
						f.set(dataResultCopy, "FF");
					} else {
						f.set(dataResultCopy, value);
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();

		}
		return dataResultCopy;
	}
	
	
	
	public static String toJson(String [] condition,String [] value){
		String str_json = "";
		 String str1 = "{\"";
		 String str2 = "}";
		 String str3 = "\":\"";
		 String str4 = "\",\"";
		for(int i=0;i<condition.length;i++){
		 str1 = str1 + condition[i]+str3+value[i]+str4;
		}
		if(str1.length()>10){
			str_json = str1.substring(0, str1.length()-2)+str2;
		}
		return str_json;	
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
