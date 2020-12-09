package com.callpneck.Language;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.callpneck.R;

import java.util.List;

public class CountryListArrayAdapter extends ArrayAdapter<Country> {
    private final List<Country> list;
    private final Activity context;
    private String code="es";
    static class ViewHolder {
        protected TextView name;
        protected ImageView flag;
        protected ImageView tick;
    }
    public CountryListArrayAdapter(Activity context, List<Country> list) {
        super(context, R.layout.activity_country_row, list);
        this.context = context;
        this.list = list;
        code = AppThemePrefrences.GetSharedPreference(context, AppThemePrefrences.APP_LANGAUGE);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.activity_country_row, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.name = (TextView) view.findViewById(R.id.name);
            viewHolder.flag = (ImageView) view.findViewById(R.id.flag);
            viewHolder.tick = (ImageView) view.findViewById(R.id.check_icon);
            view.setTag(viewHolder);
        } else {
            view = convertView;
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.name.setText(list.get(position).getName());
        holder.flag.setImageResource(AppUtils.getRawResourceID(context,list.get(position).getCode()));
        if(list.get(position).getCode().equals(code))
        {
            holder.tick.setVisibility(View.VISIBLE);
            // holder.name.setTextColor(Color.parseColor("#EE3313"));
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else {
            holder.tick.setVisibility(View.GONE);
            holder.name.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            holder.name.setTypeface(Typeface.DEFAULT_BOLD);
            //  holder.name.setTextColor(Color.parseColor("#333333"));
        }
        return view;
    }
}