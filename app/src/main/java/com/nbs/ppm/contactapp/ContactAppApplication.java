package com.nbs.ppm.contactapp;

import android.app.Application;

import com.nbs.ppm.contactapp.model.ItemContact;

/**
 * Created by Sidiq on 18/06/2015.
 */
public class ContactAppApplication extends Application {
    ItemContact itemContact;

    @Override
    public void onCreate() {
        super.onCreate();
        itemContact = new ItemContact();
    }

    public ItemContact getItemContact(){
        return  itemContact;
    }
}
