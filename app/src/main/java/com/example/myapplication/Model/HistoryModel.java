package com.example.myapplication.Model;

public class HistoryModel {
    String waterPercentage;
    String date;
    String time;
    String id;

    public HistoryModel(String waterPercentage, String date, String time, String id){
        this.waterPercentage = waterPercentage;
        this.date = date;
        this.time = time;
        this.id = id;
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
    public String getId(){
        return id;
    }
}
