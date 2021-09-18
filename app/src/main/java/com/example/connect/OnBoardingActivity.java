package com.example.connect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.connect.Adapter.OnBoardingAdapter;

import org.w3c.dom.Text;

public class OnBoardingActivity extends AppCompatActivity {

    ViewPager onBoardingViewPager;
    Button next, back;
    TextView[] dots;
    LinearLayout endLayout;
    int currentPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_boarding);
        onBoardingViewPager = findViewById(R.id.onBoarding_viewPager);
        OnBoardingAdapter onBoardingAdapter = new OnBoardingAdapter(getSupportFragmentManager(), this, 2);
        onBoardingViewPager.setAdapter(onBoardingAdapter);

        endLayout = findViewById(R.id.endLayout);
        next = findViewById(R.id.btn_next);
        back = findViewById(R.id.btn_back);
        addDotsIndicator(0);

        onBoardingViewPager.addOnPageChangeListener(viewListener);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBoardingViewPager.setCurrentItem(currentPage + 1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBoardingViewPager.setCurrentItem(currentPage - 1);
            }
        });
    }

    public void addDotsIndicator(int position){
        dots = new TextView[2];
        endLayout.removeAllViews();

        for (int i = 0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorGray));
            dots[i].setGravity(View.TEXT_ALIGNMENT_CENTER);


            endLayout.addView(dots[i]);

        }

        if (dots.length > 0){
            dots[position].setTextColor(getResources().getColor(R.color.colorBlue));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

            addDotsIndicator(position);
            currentPage = position;

            if (position == 0){
               // next.setText("NEXT");
                next.setEnabled(true);
                back.setVisibility(View.INVISIBLE);
              //  next.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            }
            else if (position < dots.length){
                //next.setEnabled(false);
                //next.setText("Let's Go");
                //next.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                back.setVisibility(View.VISIBLE);
            }
            else {
                //next.setText("NEXT");
                next.setEnabled(true);
                back.setVisibility(View.VISIBLE);
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };
}