package com.example.samuel.at_bristol_app.models;

import android.media.Image;
import android.os.Parcel;
import android.os.Parcelable;

import com.example.samuel.at_bristol_app.activities.RFIDListActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The model for a Visit list element, contains all the relevant functions and data
 */

public class VisitModel{
    private Date date;
    private List<String> rfidStringList;

    public VisitModel(Date date, List<String> rfidStringList){
        this.date = date;
        this.rfidStringList = rfidStringList;
    }

    public ArrayList<String> getRFIDList(){
        return (ArrayList<String>) rfidStringList;
    }

    public int getRFIDCount(){
        return rfidStringList.size();
    }

    public Date getDate(){
        return date;
    }


}
