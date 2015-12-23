package com.beautyhealth.PrivateDoctors.Entity;

import java.util.List;

public class MedicalIssue {
	public String auto_id;
	public String QuestionContent;
	public String UserID;
	public String QuestionTime;
	public String DoctorType;
	public String IsShared;
	public List<Object> Reply;
       	
	
	public String getAuto_id() {
		return auto_id;
	}
	public void setAuto_id(String auto_id) {
		this.auto_id = auto_id;
	}
	public String getQuestionContent() {
		return QuestionContent;
	}
	public void setQuestionContent(String questionContent) {
		QuestionContent = questionContent;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getQuestionTime() {
		return QuestionTime;
	}
	public void setQuestionTime(String questionTime) {
		QuestionTime = questionTime;
	}
	public String getDoctorType() {
		return DoctorType;
	}
	public void setDoctorType(String doctorType) {
		DoctorType = doctorType;
	}
	public String getIsShared() {
		return IsShared;
	}
	public void setIsShared(String isShared) {
		IsShared = isShared;
	}

	public List<Object> getReply() {
		return Reply;
	}
	public void setReply(List<Object> reply) {
		Reply = reply;
	}

}
