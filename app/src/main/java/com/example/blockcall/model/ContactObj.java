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

    @Exclude
    public int getId() {
        return id;
    }
    @Exclude
    public void setId(int id) {
        this.id = id;
    }
    @Exclude
    public String getUserName() {
        return userName;
    }
    @Exclude
    public void setUserName(String userName) {
        this.userName = userName;
    }
    @Exclude
    public String getPhoneNum() {
        return phoneNum;
    }
    @Exclude
    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }
    @Exclude
    public String getDateBlock() {
        return dateBlock;
    }
    @Exclude
    public void setDateBlock(String dateBlock) {
        this.dateBlock = dateBlock;
    }
    @Exclude
    public String getTimeBlock() {
        return timeBlock;
    }
    @Exclude
    public void setTimeBlock(String timeBlock) {
        this.timeBlock = timeBlock;
    }
}
