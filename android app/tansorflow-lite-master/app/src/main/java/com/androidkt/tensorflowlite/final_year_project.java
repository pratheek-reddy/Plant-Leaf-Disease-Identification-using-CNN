package com.androidkt.tensorflowlite;

import android.app.Application;
import android.os.Bundle;

import com.google.firebase.FirebaseApp;

/**
 * Created by vivek on 12-05-2020.
 */

public class final_year_project extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);

    }
}