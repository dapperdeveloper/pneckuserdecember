package com.callpneck.activity.registrationSecond.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.callpneck.R;
import com.callpneck.activity.registrationSecond.MainScreenActivity;
import com.callpneck.activity.registrationSecond.Model.OrderTracker;
import com.callpneck.activity.registrationSecond.fragment.OrderTrackerListFragment;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderListActivity extends AppCompatActivity {

    LinearLayout lytempty, lytdata;

    public static ArrayList<OrderTracker> orderTrackerslist, cancelledlist, deliveredlist, processedlist, shippedlist, returnedList;
    String[] tabs;
    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.order_track));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabs = new String[]{getString(R.string.all), getString(R.string.in_process1), getString(R.string.shipped1), getString(R.string.delivered1), getString(R.string.cancelled1), getString(R.string.returned1)};
        lytempty = findViewById(R.id.lytempty);
        lytdata = findViewById(R.id.lytdata);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setOffscreenPageLimit(5);
        tabLayout = findViewById(R.id.tablayout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OrderTrackerListFragment(), tabs[0]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[1]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[2]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[3]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[4]);
        adapter.addFrag(new OrderTrackerListFragment(), tabs[5]);
        viewPager.setAdapter(adapter);
    }
    public static class ViewPagerAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        }

        @Override
        public Fragment getItem(int position) {
            Bundle data = new Bundle();
            OrderTrackerListFragment fragment = new OrderTrackerListFragment();
            data.putInt("pos", position);
            fragment.setArguments(data);
            return fragment;
        }

        @Override
        public int getCount() {

            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


    }


    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.scale_to_center, R.anim.push_down_out);

    }

    public void OnBtnClick(View view) {
        Intent refresh = new Intent(OrderListActivity.this, MainScreenActivity.class);
        refresh.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Set this flag
        startActivity(refresh);
        finish();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}