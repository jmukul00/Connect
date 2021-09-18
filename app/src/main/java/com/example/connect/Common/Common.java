package com.example.connect.Common;

import com.example.connect.Model.PostModel;
import com.example.connect.Model.UserInterestModel;
import com.example.connect.Model.UserModel;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Common {
    public static UserModel currentUser;
    public static final String USER_REFERENCES = "Users";
    public static final String INTEREST_REFERENCES = "Interests";
    public static final String POST_REFERENCES = "Posts";
    public static PostModel selectedPost;
    public static List<String> userInterestList = new ArrayList<String>();
    public static List<String> userFollowingList = new ArrayList<String>();
    public static String SELECTED_PERSON_UID = "";
    public static  boolean incViews = false;




}
