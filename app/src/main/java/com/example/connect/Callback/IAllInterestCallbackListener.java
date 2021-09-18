package com.example.connect.Callback;

import com.example.connect.Model.InterestModel;


import java.util.List;

public interface IAllInterestCallbackListener {

    void onAllInterestLoadSuccess(List<InterestModel> allInterestModels);

    void onAllInterestLoadFailed(String message);
}
