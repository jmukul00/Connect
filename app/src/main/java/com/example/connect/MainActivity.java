package com.example.connect;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.connect.Adapter.ViewPagerAdapter;
import com.example.connect.Common.InternetConnect;
import com.example.connect.ui.Home.HomeFragment;
import com.example.connect.ui.Home.MainFragment;
import com.example.connect.ui.Posts.CreatePostFragment;
import com.example.connect.ui.Posts.ShowPostFragment;
import com.example.connect.ui.Posts.WebViewFragment;
import com.example.connect.ui.Profile.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements FragmentManager.OnBackStackChangedListener {

    public BottomNavigationView navigationView;
    private long backPressed ;
    InternetConnect internetConnect;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



       navigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.addOnBackStackChangedListener(this);
        internetConnect = new InternetConnect();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        internetConnect = new InternetConnect();
        registerReceiver(internetConnect, intentFilter);
        loadFirstFragment(new MainFragment());
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {

                Fragment fragment;
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                            fragment = new MainFragment();
                        loadFirstFragment(fragment);
                        return true;

                    case R.id.navigation_profile:
                            fragment = new ProfileFragment();
                        loadFirstFragment(fragment);
                            return true;
                }

                return false;
            };



    private void loadFirstFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.commit();
    }

    private void loadFragment(Fragment fragment) {

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }


    @Override
    protected void onStart() {
        super.onStart();

        //EventBus.getDefault().register(this);

    }

    @Override
    protected void onStop() {
        super.onStop();

        //EventBus.getDefault().unregister(this);
    }


    @Override
    public void onBackStackChanged() {
        Fragment f =getSupportFragmentManager().findFragmentById(R.id.frame_container);
        if (f instanceof HomeFragment) {
            navigationView.setVisibility(View.VISIBLE);
        } else if (f instanceof CreatePostFragment) {
            navigationView.setVisibility(View.GONE);
        }else if (f instanceof WebViewFragment) {
            navigationView.setVisibility(View.GONE);
        } else if (f instanceof ShowPostFragment) {
            navigationView.setVisibility(View.GONE);
        }else if (f instanceof ProfileFragment) {
            navigationView.setVisibility(View.VISIBLE);
        }else {
            navigationView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment f =getSupportFragmentManager().findFragmentById(R.id.frame_container);

        if(f instanceof HomeFragment){
            if (backPressed + 2000 > System.currentTimeMillis())
            {
                super.onBackPressed();
                return;
            }

            else {
                Toast.makeText(this, "Press Back Again", Toast.LENGTH_SHORT).show();
            }

            backPressed  = System.currentTimeMillis();

        }
        /*else if (f instanceof ShowPostFragment){
           loadFirstFragment(new ProfileFragment());
        }*/ else if (f instanceof WebViewFragment){
            if (getFragmentManager().getBackStackEntryCount() != 0) {
                getFragmentManager().popBackStack();
            }
        }

    }
}