package com.beautyhealth.PrivateDoctors.Assistant;

import java.util.Comparator;

import com.beautyhealth.PrivateDoctors.Entity.MedicalIssue;
import com.beautyhealth.PrivateDoctors.Entity.Reply;

public class ReplySort implements Comparator{  
	
    public int compare(Object o1,Object o2){  
    	Reply p1 = (Reply) o1;  
    	Reply p2 = (Reply) o2; 
    	int result=p1.auto_id.compareTo(p2.auto_id);
        return result;  
    }  
}
