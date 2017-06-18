package com.frontend.coffeeenglish;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.io.IOException;
//C:\Users\jeong\apps\appengine-endpoints-helloendpoints-android-master\HelloEndpointsProject\HelloEndpoints\libs\android-sqlite-asset-helper.jar
public class DBManager extends SQLiteAssetHelper  {
    private static final String DATABASE_NAME = "Question12";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DATABASES";

    public DBManager(Context context) throws IOException {
        //super(context);//, "question.sqlite", null, DATABASE_VERSION);
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

    }


}
