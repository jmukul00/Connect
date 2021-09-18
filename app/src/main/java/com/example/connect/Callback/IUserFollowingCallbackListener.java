package com.example.connect.Callback;

import com.example.connect.Model.UserFollowingModel;
import com.example.connect.Model.UserInterestModel;

import java.util.List;

public interface IUserFollowingCallbackListener {

    void onAllUserFollowingLoadSuccess(List<UserFollowingModel> allUserFollowingModels);

    void onAllUserFollowingLoadFailed(String message);

}
