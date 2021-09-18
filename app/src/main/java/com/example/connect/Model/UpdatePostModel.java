package com.example.connect.Model;

import java.util.List;

public class UpdatePostModel {

    private List<PostModel> postModelList;
    public UpdatePostModel() {
    }


    public UpdatePostModel(List<PostModel> postModelList) {
        this.postModelList = postModelList;
    }

    public List<PostModel> getPostModelList() {
        return postModelList;
    }

    public void setPostModelList(List<PostModel> postModelList) {
        this.postModelList = postModelList;
    }
}
