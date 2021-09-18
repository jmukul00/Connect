package com.example.connect.Model;

import java.util.Map;

public class PostModel {
    private String postTitle, postImage, postLink, postDesc, postCategory,
            postPersonName, postPersonUid, postId;

    private int postStatus, postViews;

    private Map<String, Object> postTimeStamp;

    public PostModel() {
    }

    public String getPostTitle() {
        return postTitle;
    }

    public void setPostTitle(String postTitle) {
        this.postTitle = postTitle;
    }

    public String getPostImage() {
        return postImage;
    }

    public void setPostImage(String postImage) {
        this.postImage = postImage;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    public String getPostDesc() {
        return postDesc;
    }

    public void setPostDesc(String postDesc) {
        this.postDesc = postDesc;
    }

    public String getPostCategory() {
        return postCategory;
    }

    public void setPostCategory(String postCategory) {
        this.postCategory = postCategory;
    }

    public String getPostPersonName() {
        return postPersonName;
    }

    public void setPostPersonName(String postPersonName) {
        this.postPersonName = postPersonName;
    }

    public String getPostPersonUid() {
        return postPersonUid;
    }

    public void setPostPersonUid(String postPersonUid) {
        this.postPersonUid = postPersonUid;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public Map<String, Object> getPostTimeStamp() {
        return postTimeStamp;
    }

    public void setPostTimeStamp(Map<String, Object> postTimeStamp) {
        this.postTimeStamp = postTimeStamp;
    }

    public int getPostStatus() {
        return postStatus;
    }

    public void setPostStatus(int postStatus) {
        this.postStatus = postStatus;
    }

    public int getPostViews() {
        return postViews;
    }

    public void setPostViews(int postViews) {
        this.postViews = postViews;
    }


}
