package com.callpneck.Language;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.callpneck.R;
import com.callpneck.activity.registrationSecond.MainScreenActivity;

import java.util.ArrayList;
import java.util.Locale;
public class LanguageSettingActivity extends AppCompatActivity {
    private String app_language;
    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.setLanguage(this);
        setContentView(R.layout.activity_language_setting);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.select_lang);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        final ArrayList countryList = DialogUtills.populateCountryList(this);
        ArrayAdapter<Country> adapter = new CountryListArrayAdapter(this, countryList);
        ListView listView = (ListView) findViewById(R.id.countrylistView3);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country c = (Country) countryList.get(position);
                AppThemePrefrences.SetSharedPreference(getApplicationContext(), "L", c.getName());
                AppThemePrefrences.SetSharedPreference( getApplicationContext(), AppThemePrefrences.APP_LANGAUGE, c.getCode());
                ThemeUtils.setLanguage(getApplicationContext());
                app_language = AppThemePrefrences.GetSharedPreference(getApplicationContext(),AppThemePrefrences.APP_LANGAUGE);
                AppThemePrefrences.SetBooleanSharedPreference(getApplicationContext(), AppThemePrefrences.TAB_ORDER,true);
//                myLocale = new Locale(c.getCode());
//                Resources res = getResources();
//                DisplayMetrics dm = res.getDisplayMetrics();
//                Configuration conf = res.getConfiguration();
//                conf.locale = myLocale;
//                res.updateConfiguration(conf, dm);
                Intent refresh = new Intent(LanguageSettingActivity.this, MainScreenActivity.class);
                refresh.putExtra(app_language, c.getCode());
                refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Set this flag
                startActivity(refresh);
                finish();
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED,returnIntent);
        finish();
        super.onBackPressed();
        overridePendingTransition(R.anim.in_from_top, R.anim.out_from_bottom);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(menuItem);
    }
    public  void setLanguage() {
        try {
            Resources res = getResources();
            DisplayMetrics dm = res.getDisplayMetrics();
            android.content.res.Configuration conf = res.getConfiguration();
            String language = AppThemePrefrences.GetSharedPreference( getApplicationContext(), AppThemePrefrences.APP_LANGAUGE);
            if ((!(language == null)) || !(language.equalsIgnoreCase(""))) {
                String[] arr = language.split("_");
                if (arr.length > 1) {
                    conf.locale = new Locale(arr[0], arr[1].toUpperCase());
                } else {
                    conf.locale = new Locale(language.toLowerCase());
                }
                res.updateConfiguration(conf, dm);
            }
        } catch (Exception e) {
            Log.d("Error ", e.toString());
        }
    }

public static void setLanguage(Context context) {
    try {
        Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        String language = AppThemePrefrences.GetSharedPreference(context, AppThemePrefrences.APP_LANGAUGE);
        if ((!(language == null)) || !(language.equalsIgnoreCase(""))) {
            String[] arr = language.split("_");
            if (arr.length > 1) {
                conf.locale = new Locale(arr[0], arr[1].toUpperCase());
            } else {
                conf.locale = new Locale(language.toLowerCase());
            }
            res.updateConfiguration(conf, dm);
        }
    } catch (Exception e) {
        Log.d("Error ", e.toString());
    }
}


}