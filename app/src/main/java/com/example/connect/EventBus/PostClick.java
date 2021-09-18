package com.example.connect.EventBus;


import com.example.connect.Model.PostModel;

public class PostClick {
    private boolean success;
    private PostModel postModel;

    public PostClick(boolean success, PostModel postModel) {
        this.success = success;
        this.postModel = postModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public PostModel getPostModel() {
        return postModel;
    }

    public void setPostModel(PostModel postModel) {
        this.postModel = postModel;
    }
}
