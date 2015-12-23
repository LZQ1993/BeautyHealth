package com.beautyhealth.PrivateDoctors.Assistant;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.beautyhealth.Infrastructure.CWDataDecode.DataResult;
import com.beautyhealth.PrivateDoctors.Entity.MedicalIssue;
import com.beautyhealth.PrivateDoctors.Entity.Reply;

public class QuestionDecode {

	public Object fromJson(String json) {

		Object dataResultCopy = null;
		Object dataResultCopySon = null;
		Object dataResultCopySons = null;

		Class<?> c = null;
		Class<?> cs = null;
		Class<?> css = null;

		try {
			c = Class
					.forName("com.beautyhealth.Infrastructure.CWDataDecode.DataResult");
			cs = Class
					.forName("com.beautyhealth.PrivateDoctors.Entity.MedicalIssue");
			css = Class.forName("com.beautyhealth.PrivateDoctors.Entity.Reply");

		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Field[] objectFs = c.getFields();
		Field[] sonObjectFs = cs.getFields();
		Field[] sonsObjectFs = css.getFields();

		try {
			JSONObject jsonObject = new JSONObject(json);
			try {
				dataResultCopy = c.newInstance();
			} catch (InstantiationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IllegalAccessException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
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
						JSONObject jsonObjectSon = (JSONObject) jsonArray.opt(j);
						try {
							dataResultCopySon = cs.newInstance();
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						for (int k = 0; k < sonObjectFs.length; k++) {
							Field fson = sonObjectFs[k];
							String fieldSonName = fson.getName();
							if (fieldSonName.equals("Reply")) {
								JSONArray jsonArrays = jsonObjectSon.getJSONArray(fieldSonName);
								List<Object> listss= new ArrayList<Object>();
								for (int jj = 0; jj < jsonArrays.length(); jj++) {
									JSONObject jsonObjectSons = (JSONObject) jsonArrays.opt(jj);
									try {
										dataResultCopySons = css.newInstance();
									} catch (InstantiationException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									for (int kk = 0; kk < sonsObjectFs.length; kk++) {
										Field fsons = sonsObjectFs[kk];
										String fieldSonsName = fsons.getName();
										try {
											value = jsonObjectSons.optString(fieldSonsName);
											
										} catch (Exception ex) {
											value = null;
										}
										if (value == null || value == "") {
											value = "FF";
											try {
												fsons.set(dataResultCopySons, "FF");
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IllegalArgumentException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										} else {
											try {
												fsons.set(dataResultCopySons, value);
											} catch (IllegalAccessException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											} catch (IllegalArgumentException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
										}
										
									}
									listss.add(dataResultCopySons);
								}
								try {
									fson.set(dataResultCopySon, listss);
								} catch (IllegalAccessException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IllegalArgumentException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							} else {
								try {
									value = jsonObjectSon.optString(fieldSonName);
								} catch (Exception ex) {
									value = null;
								}
								if (value == null || value == "") {
									value = "FF";
									try {
										fson.set(dataResultCopySon, "FF");
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								} else {
									try {
										fson.set(dataResultCopySon, value);
									} catch (IllegalAccessException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (IllegalArgumentException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}
							try {
								fson.set(dataResultCopySon, value);
							} catch (IllegalAccessException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						lists.add(dataResultCopySon);
					}
					try {
						f.set(dataResultCopy, lists);
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						value = jsonObject.optString(fieldName);
					} catch (Exception ex) {
						value = null;
					}
					if (value == null || value == "") {
						value = "FF";
						try {
							f.set(dataResultCopy, "FF");
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} else {
						try {
							f.set(dataResultCopy, value);
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return dataResultCopy;
	}

}
