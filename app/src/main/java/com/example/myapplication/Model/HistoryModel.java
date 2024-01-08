package com.example.myapplication.Model;

public class HistoryModel {
    String waterPercentage;
    String date;
    String time;

    public HistoryModel(String waterPercentage, String date, String time){
        this.waterPercentage = waterPercentage;
        this.date = date;
        this.time = time;
    }

    public String getWaterPercentage() {
        return waterPercentage;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}
