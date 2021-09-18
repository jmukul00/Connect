package com.example.connect.Callback;

import com.example.connect.Model.InterestModel;
import com.example.connect.Model.PostModel;

import java.util.List;

public interface IMyPostCallbackListener {

    void onAllPostLoadSuccess(List<PostModel> allPostModels);

    void onAllPostLoadFailed(String message);

}
