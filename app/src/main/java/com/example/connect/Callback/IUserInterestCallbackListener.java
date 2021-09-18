package com.example.connect.Callback;

import com.example.connect.Model.InterestModel;
import com.example.connect.Model.UserInterestModel;

import java.util.List;

public interface IUserInterestCallbackListener {

    void onAllUserInterestLoadSuccess(List<UserInterestModel> allUserInterestModels);

    void onAllUserInterestLoadFailed(String message);

}
