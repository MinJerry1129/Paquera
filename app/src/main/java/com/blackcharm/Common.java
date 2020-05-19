package com.blackcharm;

public class Common {
    private static Common instance = new Common();
    public static Common getInstance()
    {
        return instance;
    }
    private String membership_status ="no";

    public String getMembership_status() {return membership_status;}
    public void setMembership_status(String membership_status) { this.membership_status = membership_status;}
}
