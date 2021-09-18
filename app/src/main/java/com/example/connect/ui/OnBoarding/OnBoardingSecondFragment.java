package com.example.connect.ui.OnBoarding;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connect.R;

public class OnBoardingSecondFragment extends Fragment {

    public OnBoardingSecondFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout_my_post for this fragment
        return inflater.inflate(R.layout.fragment_on_boarding_second, container, false);
    }
}