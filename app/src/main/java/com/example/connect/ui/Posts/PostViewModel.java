package com.example.connect.ui.Posts;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.connect.Common.Common;
import com.example.connect.Model.PostModel;

public class PostViewModel extends ViewModel {
    private MutableLiveData<PostModel> mutableLiveDataPosts;


    public MutableLiveData<PostModel> getMutableLiveDataProducts(){
        if (mutableLiveDataPosts == null)
            mutableLiveDataPosts = new MutableLiveData<>();
        mutableLiveDataPosts.setValue(Common.selectedPost);
        return mutableLiveDataPosts;
    }


}
