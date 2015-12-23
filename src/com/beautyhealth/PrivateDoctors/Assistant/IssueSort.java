package com.beautyhealth.PrivateDoctors.Assistant;

import java.util.Comparator;

import com.beautyhealth.PrivateDoctors.Entity.MedicalIssue;

public class IssueSort implements Comparator{  
	
    public int compare(Object o1,Object o2){  
    	MedicalIssue p1 = (MedicalIssue) o1;  
    	MedicalIssue p2 = (MedicalIssue) o2; 
    	/*DateFormat df=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	Calendar c1=Calendar.getInstance();
    	Calendar c2=Calendar.getInstance();
    	try {
			c1.setTime(df.parse(p1.QuestionTime));	
        	c2.setTime(df.parse(p2.QuestionTime));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
    	int result=p1.auto_id.compareTo(p2.auto_id);
        return result;  
    }  
}  
