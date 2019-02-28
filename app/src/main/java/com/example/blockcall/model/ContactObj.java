package com.example.blockcall.model;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class ContactObj {

    public int id;
    public String userName;
    public String phoneNum;
    public String dateBlock;
    public String timeBlock;

    public ContactObj() {
    }

    public ContactObj(int id, String userName, String phoneNum) {
        this.id = id;
        this.userName = userName;
        this.phoneNum = phoneNum;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPhoneNum() {
        return phoneNum;
    }
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    public String getDateBlock() {
        return dateBlock;
    }
    public void setDateBlock(String dateBlock) {
        this.dateBlock = dateBlock;
    }
    public String getTimeBlock() {
        return timeBlock;
    }
    public void setTimeBlock(String timeBlock) {
        this.timeBlock = timeBlock;
    }
}
