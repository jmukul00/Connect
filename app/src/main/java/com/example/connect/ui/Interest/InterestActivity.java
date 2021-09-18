package com.example.connect.ui.Interest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.connect.Adapter.InterestAdapter;
import com.example.connect.Common.Common;
import com.example.connect.Common.GridSpacingItemDecoration;
import com.example.connect.MainActivity;
import com.example.connect.Model.InterestModel;
import com.example.connect.Model.UserInterestModel;
import com.example.connect.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class InterestActivity extends AppCompatActivity {

    Unbinder unbinder;
   private InterestViewModel interestViewModel;
    InterestAdapter interestAdapter;

    private List<InterestModel> mModelList;
    @BindView(R.id.rv_interest)
    RecyclerView recyclerViewInterest;
    private DatabaseReference userRef;

    Button btn_next;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interest);

        interestViewModel = new ViewModelProvider(this).get(InterestViewModel.class);
        unbinder = ButterKnife.bind(this);
        userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES).
                child(Common.currentUser.getUid()).child("interest");
        btn_next = findViewById(R.id.btn_interest_next);
        btn_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (interestAdapter.getSelected().size() >= 3){
                    for (int i = 0; i< interestAdapter.getSelected().size(); i++){
                        final List<UserInterestModel>  selectableItems = new ArrayList<>();
                        //selectableItems.add(new UserInterestModel(interestAdapter.getSelected().get(i).getName()));
                        UserInterestModel userInterestModel = new UserInterestModel();
                        userInterestModel.setName(interestAdapter.getSelected().get(i).getName());
                        //stringBuilder.append(interestAdapter.getSelected().get(i).getName());
                        userRef.push().setValue(userInterestModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                startActivity(new Intent(InterestActivity.this, MainActivity.class));
                                finish();
                            }
                        });
                    }

                }

                else {
                    Toast.makeText(InterestActivity.this, "Select Atleast 3", Toast.LENGTH_SHORT).show();
                }
            }
        });
        interestViewModel.getAllInterestList().observe(this, new Observer<List<InterestModel>>() {
            @Override
            public void onChanged(List<InterestModel> interestModels) {
                interestAdapter = new InterestAdapter(InterestActivity.this.getApplicationContext(), interestModels);
                recyclerViewInterest.setAdapter(interestAdapter);
            }
        });

        init();

    }

    private void init() {

      /*  ((AppCompatActivity)getActivity())
                .getSupportActionBar()
                .setTitle("Categories");*/

        int spanCount = 3; // 3 columns
        int spacing = 25; // 50px
        boolean includeEdge = false;

        GridLayoutManager layoutManager= new GridLayoutManager(getApplicationContext(), 3);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewInterest.setLayoutManager(layoutManager);
        recyclerViewInterest.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

    }
}