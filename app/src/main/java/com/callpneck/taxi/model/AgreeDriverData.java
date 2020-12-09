package com.callpneck.taxi.model;

public class AgreeDriverData {
    private String id,bookibgId,employeeId,epToken,employeeLat,employeeLong,employeeName,employeePhone,employeeTimeToReach,description,status,empCashOffered;

    public AgreeDriverData(String id, String bookibgId, String employeeId, String epToken, String employeeLat, String employeeLong, String employeeName, String employeePhone, String employeeTimeToReach, String description, String status, String empCashOffered) {
        this.id = id;
        this.bookibgId = bookibgId;
        this.employeeId = employeeId;
        this.epToken = epToken;
        this.employeeLat = employeeLat;
        this.employeeLong = employeeLong;
        this.employeeName = employeeName;
        this.employeePhone = employeePhone;
        this.employeeTimeToReach = employeeTimeToReach;
        this.description = description;
        this.status = status;
        this.empCashOffered = empCashOffered;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBookibgId() {
        return bookibgId;
    }

    public void setBookibgId(String bookibgId) {
        this.bookibgId = bookibgId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEpToken() {
        return epToken;
    }

    public void setEpToken(String epToken) {
        this.epToken = epToken;
    }

    public String getEmployeeLat() {
        return employeeLat;
    }

    public void setEmployeeLat(String employeeLat) {
        this.employeeLat = employeeLat;
    }

    public String getEmployeeLong() {
        return employeeLong;
    }

    public void setEmployeeLong(String employeeLong) {
        this.employeeLong = employeeLong;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public void setEmployeePhone(String employeePhone) {
        this.employeePhone = employeePhone;
    }

    public String getEmployeeTimeToReach() {
        return employeeTimeToReach;
    }

    public void setEmployeeTimeToReach(String employeeTimeToReach) {
        this.employeeTimeToReach = employeeTimeToReach;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEmpCashOffered() {
        return empCashOffered;
    }

    public void setEmpCashOffered(String empCashOffered) {
        this.empCashOffered = empCashOffered;
    }
}
