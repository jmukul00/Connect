package com.example.connect.Model;

import java.util.List;

public class InterestModel {
    private String image, interest_id, name;
    private boolean isSelected = false;



    public InterestModel() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getInterest_id() {
        return interest_id;
    }

    public void setInterest_id(String interest_id) {
        this.interest_id = interest_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }


}
