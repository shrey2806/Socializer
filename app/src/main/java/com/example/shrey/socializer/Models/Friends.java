package com.example.shrey.socializer.Models;

public class Friends {
    public Friends(){

    }

    public Friends(String date) {
        this.date = date;
    }

    public String date;


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
