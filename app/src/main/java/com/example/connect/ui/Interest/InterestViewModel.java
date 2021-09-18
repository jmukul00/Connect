package com.example.connect.ui.Interest;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.connect.Callback.IAllInterestCallbackListener;
import com.example.connect.Common.Common;
import com.example.connect.Model.InterestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InterestViewModel extends ViewModel implements IAllInterestCallbackListener {
    private MutableLiveData<List<InterestModel>> allInterestList;
    private MutableLiveData<String> messageError;
    private IAllInterestCallbackListener iAllInterestCallbackListener;

    public InterestViewModel() {
       iAllInterestCallbackListener = this;
    }

    public MutableLiveData<List<InterestModel>> getAllInterestList() {
        if (allInterestList == null)
        {
            allInterestList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadAllInterestList();
        }
        return allInterestList;
    }

    private void loadAllInterestList() {
        final List<InterestModel> interestModelList = new ArrayList<>();
        final DatabaseReference interestRef = FirebaseDatabase.getInstance().getReference(Common.INTEREST_REFERENCES);
        interestRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    InterestModel interestModel = itemSnapshot.getValue(InterestModel.class);
                    interestModelList.add(interestModel);
                }
                iAllInterestCallbackListener.onAllInterestLoadSuccess(interestModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iAllInterestCallbackListener.onAllInterestLoadFailed(databaseError.getMessage());
            }
        });
    }

    @Override
    public void onAllInterestLoadSuccess(List<InterestModel> allInterestModels) {
        allInterestList.setValue(allInterestModels);
    }

    @Override
    public void onAllInterestLoadFailed(String message) {

    }
}
