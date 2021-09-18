package com.example.connect.ui.Profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.connect.Callback.IMyPostCallbackListener;
import com.example.connect.Common.Common;
import com.example.connect.Model.InterestModel;
import com.example.connect.Model.PostModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyPostViewModel extends ViewModel implements IMyPostCallbackListener {

    private MutableLiveData<List<PostModel>> allPostList;
    private MutableLiveData<String> messageError;

    private IMyPostCallbackListener iMyPostCallbackListener;

    public MyPostViewModel()
    {
        iMyPostCallbackListener = this;
    }

    public MutableLiveData<List<PostModel>> getAllPostList() {
        if (allPostList == null)
        {
            allPostList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadAllPostList();
        }
        return allPostList;
    }

    private void loadAllPostList() {

        final List<PostModel> postModelList = new ArrayList<>();
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES);
        postRef.orderByChild("postPersonUid").equalTo(Common.currentUser.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){
                    PostModel postModel = itemSnapshot.getValue(PostModel.class);
                    postModelList.add(postModel);
                }
                Collections.shuffle(postModelList);
                iMyPostCallbackListener.onAllPostLoadSuccess(postModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iMyPostCallbackListener.onAllPostLoadFailed(databaseError.getMessage());
            }
        });
    }


    @Override
    public void onAllPostLoadSuccess(List<PostModel> allPostModels) {
        allPostList.setValue(allPostModels);
    }

    @Override
    public void onAllPostLoadFailed(String message) {

    }
}
