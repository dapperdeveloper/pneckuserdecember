package com.callpneck.activity.registrationSecond.Model;

public class SubCategory {

    int resource;
    String subCategoryName;

    public SubCategory(int resource, String subCategoryName) {
        this.resource = resource;
        this.subCategoryName = subCategoryName;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }
}
