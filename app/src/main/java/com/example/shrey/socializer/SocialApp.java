package com.example.shrey.socializer;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shrey on 5/29/2018.
 */

public class SocialApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
//android:name="android.support.multidex.MultiDexApplication"