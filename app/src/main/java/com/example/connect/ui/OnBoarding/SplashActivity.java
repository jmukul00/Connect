package com.example.connect.ui.OnBoarding;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.VideoView;

import com.example.connect.Common.Common;
import com.example.connect.MainActivity;
import com.example.connect.Model.UserModel;
import com.example.connect.R;
import com.example.connect.ui.Interest.InterestActivity;
import com.example.connect.ui.Login.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dmax.dialog.SpotsDialog;

public class SplashActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    private android.app.AlertDialog waitingDialog;
    private DatabaseReference userRef;

    ProgressBar progressBarSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        waitingDialog = new SpotsDialog.Builder().setCancelable(false).setContext(this).build();
        firebaseAuth = FirebaseAuth.getInstance();
        progressBarSplash = findViewById(R.id.progress_splash);


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null){

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkUser(currentUser);
                }
            }, 3000);

            //waitingDialog.show();
        }

        else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    progressBarSplash.setVisibility(View.GONE);
                    finish();
                }
            }, 3000);
        }
    }

    private void checkUser(FirebaseUser currentUser) {

                userRef.child(currentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            UserModel userModel = dataSnapshot.getValue(UserModel.class);
                            if (!dataSnapshot.hasChild("interest")){
                                progressBarSplash.setVisibility(View.GONE);
                                goToInterestActivity(userModel);
                            }
                            else {
                                progressBarSplash.setVisibility(View.GONE);
                                goToMainActivity(userModel);
                            }
                            //goToMainActivity(userModel);

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void goToMainActivity(UserModel userModel) {
        Common.currentUser = userModel;
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void goToInterestActivity(UserModel userModel){
        Common.currentUser = userModel;
        Intent intent = new Intent(SplashActivity.this, InterestActivity.class);
        startActivity(intent);
        finish();
    }
}