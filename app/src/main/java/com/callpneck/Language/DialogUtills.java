package com.callpneck.Language;

import android.app.Activity;

import com.callpneck.R;

import java.util.ArrayList;

public class DialogUtills {

    public static ArrayList<Country> populateCountryList(Activity activity) {
        ArrayList<Country> countryList = new ArrayList
                <Country>();
        String countrynames[] = activity.getResources().getStringArray(R.array.country_names);
        String countrycodes [] = activity.getResources().getStringArray(R.array.country_codes);
        //  imgs = getResources().obtainTypedArray(R.array.country_flags);
        for(int i = 0; i < countrycodes.length; i++){
            countryList.add(new Country(countrynames[i], countrycodes[i]));
        }
        return  countryList;
    }
}
