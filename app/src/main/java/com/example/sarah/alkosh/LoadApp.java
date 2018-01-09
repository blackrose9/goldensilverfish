package com.example.sarah.alkosh;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

//import com.snatik.storage.Storage;
import com.sromku.simple.storage.SimpleStorage;

import ly.img.android.PESDK;

/**
 * Created by Sarah on 17/12/17.
 */

public class LoadApp extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        PESDK.init(this);




        Storage storage = SimpleStorage.getExternalStorage();
        storage.createDirectory("Alkosh");

        Bitmap hoodie = BitmapFactory.decodeResource(getResources(), R.drawable.hoodie1);
        Bitmap cup = BitmapFactory.decodeResource(getResources(), R.drawable.cup);
        Bitmap shirt  = BitmapFactory.decodeResource(getResources(), R.drawable.shirt);

        storage.createFile("Alkosh","Hoodie.jpg",hoodie);
        storage.createFile("Alkosh","Cup.jpg",cup);
        storage.createFile("Alkosh","Shirt.jpg",shirt);




    }

}