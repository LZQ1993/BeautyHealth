package com.beautyhealth.SafeGuardianship.ActionGuardianship;

public class Data {
    public String Value;
    public String MeasureTime;
    public String TimeSpan;
    public String UserID;
   
    public Data() {

	}
    
	public Data(String value, String measureTime, String timeSpan, String userID) {
		super();
		Value = value;
		MeasureTime = measureTime;
		TimeSpan = timeSpan;
		UserID = userID;
	}

	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	public String getMeasureTime() {
		return MeasureTime;
	}
	public void setMeasureTime(String measureTime) {
		MeasureTime = measureTime;
	}
	public String getTimeSpan() {
		return TimeSpan;
	}
	public void setTimeSpan(String timeSpan) {
		TimeSpan = timeSpan;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
 

  
}

