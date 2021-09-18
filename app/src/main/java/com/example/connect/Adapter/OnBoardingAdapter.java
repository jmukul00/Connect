package com.example.connect.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;

import com.example.connect.ui.OnBoarding.OnBoardingFirstFragment;
import com.example.connect.ui.OnBoarding.OnBoardingSecondFragment;

public class OnBoardingAdapter extends FragmentPagerAdapter {

    Context context;
    int totalTabs;

    public OnBoardingAdapter(@NonNull FragmentManager fm, Context context, int totalTabs) {
        super(fm);
        this.context = context;
        this.totalTabs = totalTabs;
    }



    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                OnBoardingFirstFragment onBoardingFirstFragment = new OnBoardingFirstFragment();
                return onBoardingFirstFragment;
            case 1:
                OnBoardingSecondFragment onBoardingSecondFragment = new OnBoardingSecondFragment();
                return onBoardingSecondFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

}
