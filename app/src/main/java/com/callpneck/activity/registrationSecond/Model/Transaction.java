package com.callpneck.activity.registrationSecond.Model;

public class Transaction {

    String type, userName, date, money;

    public Transaction() {
    }

    public Transaction(String type, String userName, String date, String money) {
        this.type = type;
        this.userName = userName;
        this.date = date;
        this.money = money;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}

