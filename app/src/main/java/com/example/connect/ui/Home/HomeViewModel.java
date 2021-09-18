package com.example.connect.ui.Home;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.connect.Callback.IMyPostCallbackListener;
import com.example.connect.Callback.IUserFollowingCallbackListener;
import com.example.connect.Callback.IUserInterestCallbackListener;
import com.example.connect.Common.Common;
import com.example.connect.Model.PostModel;
import com.example.connect.Model.UserFollowingModel;
import com.example.connect.Model.UserInterestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends ViewModel implements IMyPostCallbackListener, IUserInterestCallbackListener, IUserFollowingCallbackListener {


    private MutableLiveData<List<PostModel>> allPostList;
    private MutableLiveData<String> messageError;
    private static final int TOTAL_ITEM_EACH_LOAD = 10;
    private int currentPage = 0;

    private MutableLiveData<List<UserInterestModel>> userInterestModelList;
    private MutableLiveData<List<UserFollowingModel>> userFollowingModelList;

    private IMyPostCallbackListener iMyPostCallbackListener;
    private IUserInterestCallbackListener iUserInterestCallbackListener;
    private IUserFollowingCallbackListener iUserFollowingCallbackListener;

    public HomeViewModel()
    {
        iMyPostCallbackListener = this;
        iUserInterestCallbackListener = this;
        iUserFollowingCallbackListener = this;
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

    public MutableLiveData<List<PostModel>> getFollowingPostList() {
        if (allPostList == null)
        {
            allPostList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadFollowingPostList();
        }
        return allPostList;
    }



    public MutableLiveData<List<UserInterestModel>> getAllUserInterestList() {
        if (userInterestModelList == null)
        {
            userInterestModelList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadUserInterestList();
        }
        return userInterestModelList;
    }

    public MutableLiveData<List<UserFollowingModel>> getAllUserFollowingList() {
        if (userFollowingModelList == null)
        {
            userFollowingModelList = new MutableLiveData<>();
            messageError = new MutableLiveData<>();
            loadUserFollowingList();
        }
        return userFollowingModelList;
    }

    private void loadUserFollowingList() {
        final List<UserFollowingModel> userFollowingModelList = new ArrayList<>();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        userRef.child(Common.currentUser.getUid()).child("following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot interestSnapshot : dataSnapshot.getChildren()){
                    UserFollowingModel userFollowingModel = interestSnapshot.getValue(UserFollowingModel.class);
                    userFollowingModelList.add(userFollowingModel);

                }
                iUserFollowingCallbackListener.onAllUserFollowingLoadSuccess(userFollowingModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iUserFollowingCallbackListener.onAllUserFollowingLoadFailed(databaseError.getMessage());
            }
        });
    }


    private void loadAllPostList() {

        // List<InterestModel> interestList = Common.currentUser.getInterest();
        final List<PostModel> postModelList = new ArrayList<>();
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){

                                PostModel postModel = itemSnapshot.getValue(PostModel.class);
                                String postCategory = postModel.getPostCategory();

                            if (Common.userInterestList.contains(postCategory)) {
                                postModelList.add(postModel);
                         }

                        }
                        //Collections.shuffle(postModelList);
                        iMyPostCallbackListener.onAllPostLoadSuccess(postModelList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        iMyPostCallbackListener.onAllPostLoadFailed(databaseError.getMessage());
                    }
                });
    }

    private void loadFollowingPostList() {


        // List<InterestModel> interestList = Common.currentUser.getInterest();
        final List<PostModel> postModelList = new ArrayList<>();
        final DatabaseReference postRef = FirebaseDatabase.getInstance().getReference(Common.POST_REFERENCES);
        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot itemSnapshot: dataSnapshot.getChildren()){

                    PostModel postModel = itemSnapshot.getValue(PostModel.class);
                    String postPersonUid = postModel.getPostPersonUid();

                    if (Common.userFollowingList.contains(postPersonUid)) {
                        postModelList.add(postModel);
                    }

                }
                //Collections.shuffle(postModelList);
                iMyPostCallbackListener.onAllPostLoadSuccess(postModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iMyPostCallbackListener.onAllPostLoadFailed(databaseError.getMessage());
            }
        });
    }




    public void loadUserInterestList(){
        final List<UserInterestModel> userInterestModelList = new ArrayList<>();
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference(Common.USER_REFERENCES);
        userRef.child(Common.currentUser.getUid()).child("interest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot interestSnapshot : dataSnapshot.getChildren()){
                    UserInterestModel userInterestModel = interestSnapshot.getValue(UserInterestModel.class);
                    userInterestModelList.add(userInterestModel);

                }
                iUserInterestCallbackListener.onAllUserInterestLoadSuccess(userInterestModelList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                iUserInterestCallbackListener.onAllUserInterestLoadFailed(databaseError.getMessage());
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

    @Override
    public void onAllUserInterestLoadSuccess(List<UserInterestModel> allUserInterestModels) {
        userInterestModelList.setValue(allUserInterestModels);
    }

    @Override
    public void onAllUserInterestLoadFailed(String message) {

    }

    @Override
    public void onAllUserFollowingLoadSuccess(List<UserFollowingModel> allUserFollowingModels) {
        userFollowingModelList.setValue(allUserFollowingModels);
    }

    @Override
    public void onAllUserFollowingLoadFailed(String message) {

    }
}
