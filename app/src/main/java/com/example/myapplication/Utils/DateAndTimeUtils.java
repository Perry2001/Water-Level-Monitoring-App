package com.example.myapplication.Utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateAndTimeUtils {
    public static String getDateAndTimeWithSecID(){
        return new SimpleDateFormat("MMddyyyyHHmmss", Locale.getDefault()).format(new Date());
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getCurrentDate(){
        return new SimpleDateFormat("MMM DD yyyy", Locale.getDefault()).format(new Date());
    }
    public static String getCurrentTime(){
        return new SimpleDateFormat("h:mm a", Locale.getDefault()).format(new Date());
    }
}
