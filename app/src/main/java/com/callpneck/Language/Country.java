package com.callpneck.Language;

import android.graphics.drawable.Drawable;

/**
 * Created by ashish123 on 6/2/16.
 */
public class Country {
    private String name;
    private String code;
    private Drawable flag;

    public Country(String name, String code){
        this.name = name;
        this.code = code;
        this.flag = flag;
    }
    public String getName() {
        return name;
    }
    public String getCode() {
        return code;
    }
}